import { readFile, writeFile, mkdtemp, rm, stat } from "node:fs/promises";
import { tmpdir } from "node:os";
import path from "node:path";
import { pathToFileURL, fileURLToPath } from "node:url";
import { spawnSync } from "node:child_process";
import { createHash } from "node:crypto";
import { baselineFor, validate, semantic } from "./groma-gate.mjs";

const project=fileURLToPath(new URL("../../",import.meta.url));
const sha=text=>createHash("sha256").update(text).digest("hex");
const json=async file=>JSON.parse(await readFile(file,"utf8"));
function run(command,args,cwd=project) {
  const result=spawnSync(command,args,{cwd,encoding:"utf8",maxBuffer:32*1024*1024});
  if(result.status!==0)throw new Error(command+" "+args.join(" ")+": "+(result.stderr||result.error||result.stdout));
  return result.stdout.trim();
}
async function readers() {
  run("sh",["scripts/groma.sh","--version"]);
  const pin=await json(path.join(project,"tools/groma/pin.json"));
  if(Bun.version!==pin.bunVersion)throw new Error("Bun version changed; expected "+pin.bunVersion);
  let source;
  try {source=(await readFile(path.join(project,".local/groma/source.path"),"utf8")).trim();}
  catch {throw new Error("Missing verified reader source receipt; rerun sh scripts/setup-groma.sh");}
  if(run("git",["rev-parse","HEAD"],source)!==pin.commit)throw new Error("Reader source commit does not match tool pin");
  run("git",["apply","--reverse","--check",path.join(project,"tools/groma/container-fix.patch")],source);
  const sourceDiff=run("git",["diff","--binary","HEAD","--","src"],source);
  const load=relative=>import(pathToFileURL(path.join(source,relative)).href);
  return {pin,sourceDiffHash:sha(sourceDiff),
    reader:await load("src/architecture-reader.ts"), core:await load("src/core.ts"),
    model:await load("src/architecture-model.ts"), rows:await load("src/relationship-markdown.ts"),
    registry:await load("src/scanner/registry.ts"), findings:await load("src/architecture-findings.ts"),
    work:await load("node_modules/@groma/work-source-backlog/src/index.ts")};
}
export async function inspect(root,tool,observations) {
  const records=await tool.reader.loadArchitecture(root);
  const raw=tool.model.buildArchitectureModel(records.documents);
  const model=tool.core.annotateArchitecture(records);
  const rows=tool.rows.storedConnections(records.documents,raw.elements,(code,file,message)=>{throw new Error(code+": "+file+": "+message);});
  const files=model.elements.flatMap(e=>e.code.map(c=>c.file));
  const missingSources=(await Promise.all(files.map(async f=>{
    try {return (await stat(path.join(root,f))).isFile()?null:f;}catch{return f;}
  }))).filter(Boolean);
  const owners=new Map(model.elements.flatMap(e=>e.code.map(c=>[c.file,e.id])));
  const operations=observations.flatMap(o=>o.operations??[]);
  const groups=tool.findings.detectDuplicatedLogic(observations,owners);
  const duplicates=groups.map(g=>({match:g.match,members:g.instances.map(i=>{
    const bodies=operations.filter(o=>o.file===i.file&&o.name===i.name&&o.startLine===i.startLine&&o.endLine===i.endLine).map(o=>o.tokens);
    if(!bodies.length||bodies.some(b=>!b?.length))throw new Error("Missing token evidence for "+i.file+":"+i.name);
    return {file:i.file,name:i.name,tokens:bodies.sort((a,b)=>JSON.stringify(a).localeCompare(JSON.stringify(b)))};
  })}));
  const diagnosticGroups=new Map();
  for(const o of observations)for(const d of o.diagnostics) {
    const key=[o.scanner.id,d.code,d.severity].join(":");
    const group=diagnosticGroups.get(key)??{scanner:o.scanner.id,code:d.code,severity:d.severity,count:0};
    group.count++;
    const facts=d.message.match(/^(\d+) (?:calls|symbol|deferred)/);
    if(facts)group.facts=(group.facts??0)+Number(facts[1]);
    diagnosticGroups.set(key,group);
  }
  const work=await tool.work.backlogPlugin.create(root).read();
  const tasks=work.items.filter(t=>/^TASK-(22[7-9]|23[0-3]|241)$/.test(t.id)).map(t=>({
    id:t.id,refs:t.references??[],architecture:true,status:t.status
  }));
  const config=await json(path.join(root,"groma/config.json"));
  const scannerIds=observations.map(o=>o.scanner.id).sort();
  if(JSON.stringify(scannerIds)!==JSON.stringify(config.scanners.map(s=>s.id).sort()))
    throw new Error("Missing scanner observations: "+JSON.stringify(scannerIds));
  const backlinkVersion=run("backlog",["--version"],root);
  if(backlinkVersion!==config.workSources.find(s=>s.id==="backlog")?.version)
    throw new Error("Backlog CLI version differs from configured exact work-source version");
  return {elements:model.elements,rows,flows:model.flows,
    coverage:{operations:operations.length,tokenized:operations.filter(o=>o.tokens?.length).length,
      invocations:observations.reduce((n,o)=>n+(o.invocations?.length??0),0)},
    sources:[...new Set(observations.flatMap(o=>o.files.map(f=>f.file)))].sort(),
    missingSources,tasks,duplicates,diagnostics:[...diagnosticGroups.values()].sort((a,b)=>JSON.stringify(a).localeCompare(JSON.stringify(b))),
    pins:{tool:tool.pin,readerSourceDiffHash:tool.sourceDiffHash,config,scanners:observations.map(o=>o.scanner),backlogVersion:backlinkVersion}};
}
async function collect(root,tool) {
  const registry=await tool.registry.loadScannerRegistry(root);
  const batch=await registry.collectObservations(root);
  if(batch.failures.length)throw new Error("Scanner failed: "+batch.failures.map(f=>f.message).join("; "));
  if(!batch.observations.length)throw new Error("No fresh scanner evidence");
  return batch.observations;
}
export async function disposable(root,work) {
  const parent=await mkdtemp(path.join(tmpdir(),"wacken-groma-gate-"));
  const checkout=path.join(parent,"checkout");
  try {run("git",["clone","--quiet","--no-hardlinks",root,checkout]);return await work(checkout);}
  finally {await rm(parent,{recursive:true,force:true});}
}
async function main() {
  const args=process.argv.slice(2);
  const proposed=args[0]==="--propose-baseline"?path.resolve(args[1]??""):undefined;
  if(args.length&&(!proposed||args.length!==2))throw new Error("Usage: bun scripts/architecture/verify-groma.mjs [--propose-baseline /tmp/candidate.json]");
  if(proposed&&(proposed===project||proposed.startsWith(project+path.sep)))throw new Error("Baseline proposals must be outside the repository for explicit review");
  // A clean checkout must represent current product/map changes, not silently test older committed sources.
  const relevant=["groma","app/src/main","application/src/main","domain/src/main","infrastructure/src/main"];
  const pending=run("git",["status","--porcelain","--",...relevant]);
  if(pending)throw new Error("Commit reviewed product/map changes before clean-checkout validation:\n"+pending);
  const tool=await readers();
  const baselinePath=path.join(project,"tools/groma/architecture-baseline.json");
  const result=await disposable(project,async root=>{
    console.log("Inspecting disposable clean checkout with pinned Java scanner...");
    const firstEvidence=await collect(root,tool);
    const before=await inspect(root,tool,firstEvidence);
    const first=await tool.core.reconcileScanObservations(root,firstEvidence);
    const after=await inspect(root,tool,firstEvidence);
    const secondEvidence=await collect(root,tool);
    const second=await tool.core.reconcileScanObservations(root,secondEvidence);
    const repeated=await inspect(root,tool,secondEvidence);
    for(const [name,summary] of [["first",first],["second",second]]) {
      if(summary.created||summary.matched||summary.evidenceConflicts?.length||summary.scannerFailures?.length)
        throw new Error(name+" scan changed component evidence or conflicted: "+JSON.stringify(summary));
    }
    if(JSON.stringify(semantic(before,true))!==JSON.stringify(semantic(after,true))||JSON.stringify(semantic(after,true))!==JSON.stringify(semantic(repeated,true)))
      throw new Error("Scan repeatability failed: ownership, authored meaning or flows changed");
    if(proposed) {
      const candidate=baselineFor(repeated,{rationale:"REVIEW REQUIRED: classify against original audit and helper dispositions",evidence:"backlog/docs/groma-helper-dispositions-2026-10-21.md"});
      try {
        const reviewed=await json(baselinePath);
        candidate.exceptions=reviewed.exceptions;
        for(const id of candidate.duplicates)
          if(reviewed.duplicateReviews[id])candidate.duplicateReviews[id]=reviewed.duplicateReviews[id];
      } catch(error) {if(error.code!=="ENOENT")throw error;}
      candidate.review={task:"TASK-233",decision:"Standard approved by user; duplicate dispositions require explicit review"};
      await writeFile(proposed,JSON.stringify(candidate,null,2)+"\n");
      await writeFile(proposed+".snapshot.json",JSON.stringify(repeated,null,2)+"\n");
      return {errors:[],warnings:["Candidate written outside repository; no baseline accepted"],counts:{duplicateGroups:repeated.duplicates.length}};
    }
    const baseline=await json(baselinePath);
    const report=validate(repeated,baseline);
    report.repeatability={first:{created:first.created,refreshed:first.refreshed},second:{created:second.created,refreshed:second.refreshed}};
    return report;
  });
  const output=path.join(project,"build/groma-validation.json");
  await import("node:fs/promises").then(fs=>fs.mkdir(path.dirname(output),{recursive:true}));
  await writeFile(output,JSON.stringify(result,null,2)+"\n");
  console.log(JSON.stringify(result,null,2));
  if(result.errors.length)process.exitCode=1;
}
if(import.meta.main)main().catch(async error=>{
  const report={errors:["Groma gate failed: "+error.message],warnings:[]};
  const fs=await import("node:fs/promises");
  await fs.mkdir(path.join(project,"build"),{recursive:true});
  await writeFile(path.join(project,"build/groma-validation.json"),JSON.stringify(report,null,2)+"\n");
  console.error(report.errors[0]);process.exitCode=1;
});
