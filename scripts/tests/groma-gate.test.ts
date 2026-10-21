import { describe, expect, test } from "bun:test";
import { baselineFor, validate, semantic, duplicateIdentity } from "../architecture/groma-gate.mjs";
import { disposable } from "../architecture/verify-groma.mjs";
import { mkdtemp, writeFile, readFile, rm, access } from "node:fs/promises";
import { tmpdir } from "node:os";
import path from "node:path";
import { spawnSync } from "node:child_process";

function fixture() {
  return {
    elements: [
      {id:"user",kind:"actor",parent:null,title:"User",overview:"Uses the application",code:[]},
      {id:"system",kind:"system",parent:null,title:"Planner",overview:"Plans festivals",code:[]},
      {id:"app",kind:"container",parent:"system",title:"Application",overview:"Runs planner workflows",code:[]},
      ...["a","b"].map(id=>({id,kind:"component",parent:"app",title:"Role "+id,description:"Performs role "+id,
        overview:"Owns an independent responsibility "+id,code:[{file:"src/"+id+".java"}]}))
    ],
    rows:[{source:"user",target:"src/a.java",authored:true,status:"stable",description:"Starts work",technology:"UI"},
      {source:"src/a.java",target:"src/b.java",authored:true,status:"stable",description:"Delegates work",technology:"Java call"}],
    flows:[{id:"work",title:"Work",overview:"User starts delegated work",steps:[{source:"user",target:"a",action:"Start"},
      {source:"a",target:"b",action:"Delegate"}]}],
    sources:["src/a.java","src/b.java"], missingSources:[], tasks:[],
    duplicates:[{members:[{file:"src/a.java",name:"format",body:"return value.trim();"},
      {file:"src/b.java",name:"format",body:"return value.trim();"}]}],
    diagnostics:[{scanner:"java",code:"KNOWN",severity:"warning",count:2}],
    pins:{tool:{commit:"pinned"},config:{scanners:[{source:"java@1"}]}}
  };
}
function checked(change) {
  const current=fixture(), baseline=baselineFor(current);
  change(current,baseline);
  return validate(current,baseline);
}
describe("reviewed architecture gate",()=>{
  test("accepts curated baseline",()=>expect(validate(fixture(),baselineFor(fixture())).errors).toEqual([]));
  test("ordering and source line changes do not alter semantic identity",()=>{
    const x=fixture(), y=structuredClone(x);y.elements.reverse();y.rows.reverse();y.sources.reverse();
    y.elements.find(e=>e.id==="a").code[0].lines=999;y.duplicates[0].members.reverse();
    expect(semantic(x)).toEqual(semantic(y));
    expect(duplicateIdentity(x.duplicates[0])).toEqual(duplicateIdentity(y.duplicates[0]));
  });
  test("rejects a disconnected responsibility with its ID",()=>{
    expect(checked(x=>{x.rows.pop();x.flows=[];}).errors.join("\n")).toContain("island: b");
  });
  test("rejects removed owned source",()=>expect(checked(x=>x.missingSources.push("src/a.java")).errors.join("\n")).toContain("missing source: src/a.java"));
  test("rejects duplicate source ownership",()=>expect(checked(x=>x.elements[4].code.push({file:"src/a.java"})).errors.join("\n")).toContain("duplicate owner: src/a.java"));
  test("rejects new scanned source without ownership",()=>expect(checked(x=>x.sources.push("src/new.java")).errors.join("\n")).toContain("unowned scanned source: src/new.java"));
  test("rejects silently reduced scanner coverage of an existing owned source",()=>expect(checked(x=>x.sources.pop()).errors.join("\n")).toContain("owned source missing scanner evidence: src/b.java"));
  test("rejects a lost authored collaboration",()=>expect(checked(x=>x.rows.pop()).errors.join("\n")).toContain("reviewed architecture changed"));
  test("rejects an invalid current task reference",()=>expect(checked(x=>x.tasks.push({id:"TASK-233",refs:["groma:missing"]})).errors.join("\n")).toContain("task TASK-233: unknown reference missing"));
  test("rejects a plain stale ID for a known architecture task",()=>expect(checked(x=>x.tasks.push({id:"TASK-233",refs:["old-component"],architecture:true})).errors.join("\n")).toContain("task TASK-233: unknown reference old-component"));
  test("retains ordinary context references",()=>expect(checked(x=>x.tasks.push({id:"TASK-233",refs:["https://example.test","backlog/docs/context.md"]})).errors).toEqual([]));
  test("rejects recreated components even when source count matches",()=>expect(checked(x=>x.elements[4].id="b-copy").errors.join("\n")).toContain("reviewed architecture changed"));
  test("rejects broken flow",()=>expect(checked(x=>x.flows[0].steps[1].target="missing").errors.join("\n")).toContain("flow work: unknown endpoint missing"));
  test("rejects new duplicate identity even if finding count is unchanged",()=>expect(checked(x=>x.duplicates[0].members[0].body="return value.strip();").errors.join("\n")).toContain("unreviewed duplicate"));
  test("ignores duplicate source line shifts, not changed bodies",()=>{
    const x=fixture(), y=structuredClone(x); y.duplicates[0].members[0].line=123;
    expect(duplicateIdentity(x.duplicates[0])).toEqual(duplicateIdentity(y.duplicates[0]));
  });
  test("requires duplicate disposition evidence",()=>expect(checked((x,b)=>b.duplicateReviews={}).errors.join("\n")).toContain("duplicate lacks review"));
  test("rejects a new scanner category or degraded count",()=>{
    expect(checked(x=>x.diagnostics[0].count++).errors.join("\n")).toContain("scanner degradation");
    expect(checked(x=>x.diagnostics.push({scanner:"java",code:"NEW",severity:"error",count:1})).errors.join("\n")).toContain("scanner degradation");
  });
  test("rejects missing scanner evidence and changed pins",()=>{
    expect(checked(x=>x.diagnostics=[]).errors.join("\n")).toContain("missing scanner diagnostics");
    expect(checked(x=>x.pins.tool.commit="floating").errors.join("\n")).toContain("tool/scanner pin changed");
  });
  test("rejects dropped operation evidence and increased unresolved facts",()=>{
    const x=fixture();x.coverage={operations:20,tokenized:10};x.diagnostics[0].facts=5;
    const b=baselineFor(x);x.coverage.tokenized=0;x.diagnostics[0].facts=50;
    const result=validate(x,b).errors.join("\n");expect(result).toContain("scanner evidence decreased: tokenized");expect(result).toContain("scanner degradation");
  });
  test("permits only exact reviewed dangling row and reports it",()=>{
    const x=fixture();x.rows.push({source:"src/a.java",target:"src/deleted.java",authored:true,status:"stable",description:"Stale",technology:"Java"});
    const b=baselineFor(x);b.exceptions.danglingRows=[{source:"src/a.java",target:"src/deleted.java",reason:"User accepted TASK-228 limitation",evidence:"backlog/docs/evidence.md"}];
    const result=validate(x,b);expect(result.errors).toEqual([]);expect(result.warnings.join("\n")).toContain("accepted stale row");
    x.rows.at(-1).target="src/another.java";expect(validate(x,b).errors.join("\n")).toContain("dangling endpoint");
  });
  test("refuses exception with absent rationale",()=>{
    const x=fixture();const b=baselineFor(x);b.exceptions.islands=[{id:"b"}];expect(validate(x,b).errors.join("\n")).toContain("exception lacks");
  });
  test("a stale row exception cannot legitimize regained deleted ownership or a derived row",()=>{
    const x=fixture();x.rows.push({source:"src/a.java",target:"src/deleted.java",authored:true,status:"stable",description:"Stale",technology:"Java"});
    const b=baselineFor(x);b.exceptions.danglingRows=[{source:"src/a.java",target:"src/deleted.java",reason:"Accepted TASK228",evidence:"docs"}];
    x.rows.at(-1).authored=false;expect(validate(x,b).errors.join("\n")).toContain("dangling endpoint");
    x.rows.at(-1).authored=true;x.elements[4].code.push({file:"src/deleted.java"});x.sources.push("src/deleted.java");
    expect(validate(x,b).errors.join("\n")).toContain("stale exception is no longer dangling");
  });
  test("rejects duplicate IDs, empty responsibility prose and wrong container",()=>{
    expect(checked(x=>x.elements.push(structuredClone(x.elements[4]))).errors.join("\n")).toContain("duplicate element ID: b");
    expect(checked(x=>x.elements[4].overview="").errors.join("\n")).toContain("missing responsibility documentation: b");
    expect(checked(x=>x.elements[4].parent="system").errors.join("\n")).toContain("invalid containment: b");
  });
  test("explicitly reviewed file moves and combines pass with updated baseline",()=>{
    const x=fixture();x.elements[4].code[0].file="src/moved.java";x.sources[1]="src/moved.java";x.rows[1].target="src/moved.java";
    expect(validate(x,baselineFor(x)).errors).toEqual([]);
    x.elements[3].code.push(...x.elements[4].code);x.elements.pop();x.flows[0].steps.pop();
    expect(validate(x,baselineFor(x)).errors).toEqual([]);
  });
  test("fixtures are cloned; failure does not mutate baseline or caller state",()=>{
    const x=fixture(), b=baselineFor(x), saved=JSON.stringify({x,b});validate(x,b);expect(JSON.stringify({x,b})).toBe(saved);
  });
  test("disposable fixture failure preserves source checkout and removes temporary checkout",async()=>{
    const root=await mkdtemp(path.join(tmpdir(),"groma-fixture-"));let checkout;
    const git=(...args)=>{const result=spawnSync("git",args,{cwd:root,encoding:"utf8"});if(result.status)throw new Error(result.stderr);};
    try {
      git("init","--quiet");await writeFile(path.join(root,"source.txt"),"retained");git("add","source.txt");
      git("-c","user.name=Fixture","-c","user.email=fixture@example.test","commit","--quiet","-m","fixture");
      await expect(disposable(root,async target=>{checkout=target;await writeFile(path.join(target,"source.txt"),"mutated");throw new Error("injected failure");})).rejects.toThrow("injected failure");
      expect(await readFile(path.join(root,"source.txt"),"utf8")).toBe("retained");
      await expect(access(checkout)).rejects.toThrow();
    } finally {await rm(root,{recursive:true,force:true});}
  });
});
