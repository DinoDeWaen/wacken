import { createHash } from "node:crypto";

const sorted = values => [...values].sort((a,b)=>JSON.stringify(a).localeCompare(JSON.stringify(b)));
const hash = value => createHash("sha256").update(JSON.stringify(value)).digest("hex");
export const duplicateIdentity = group => hash(sorted(group.members.map(m=>({
  file:m.file,name:m.name,body:hash(m.tokens??m.body)
}))));
export function semantic(snapshot) {
  return {
    elements:sorted(snapshot.elements.map(e=>({
      id:e.id,kind:e.kind,parent:e.parent,title:e.title,description:e.description??"",
      overview:e.overview,group:e.group??"",technology:e.technology??"",
      code:sorted(e.code.map(c=>c.file))
    }))),
    rows:sorted(snapshot.rows.filter(r=>r.authored).map(r=>({
      source:r.source,target:r.target,status:r.status,description:r.description,technology:r.technology
    }))),
    flows:sorted(snapshot.flows.map(f=>({id:f.id,title:f.title,overview:f.overview,
      steps:f.steps.map(({source,target,action})=>({source,target,action}))})))
  };
}
export function baselineFor(snapshot, review={rationale:"Reviewed characterization fixture",evidence:"fixture"}) {
  return {schema:1,architecture:semantic(snapshot),pins:structuredClone(snapshot.pins),
    diagnostics:structuredClone(snapshot.diagnostics),coverage:structuredClone(snapshot.coverage??{}),duplicates:sorted(snapshot.duplicates.map(duplicateIdentity)),
    duplicateReviews:Object.fromEntries(snapshot.duplicates.map(g=>[duplicateIdentity(g),{...review,members:g.members.map(({file,name})=>({file,name}))}])),
    exceptions:{danglingRows:[],islands:[]}};
}
export function validate(snapshot, baseline) {
  const errors=[],warnings=[];
  const fail=message=>errors.push(message);
  if(baseline.schema!==1)fail("unsupported baseline schema");
  if(JSON.stringify(snapshot.pins)!==JSON.stringify(baseline.pins))fail("tool/scanner pin changed: review exact versions and baseline");
  const byId=new Map(), owners=new Map();
  for(const e of snapshot.elements) {
    if(byId.has(e.id))fail("duplicate element ID: "+e.id);
    byId.set(e.id,e);
    if(!e.title?.trim()||!e.overview?.trim()||(e.kind==="component"&&!e.description?.trim()))
      fail("missing responsibility documentation: "+e.id);
    for(const c of e.code) {
      if(owners.has(c.file))fail("duplicate owner: "+c.file+" ("+owners.get(c.file)+", "+e.id+")");
      owners.set(c.file,e.id);
    }
  }
  for(const e of snapshot.elements) {
    const expected={component:"container",container:"system"}[e.kind];
    if(expected&&byId.get(e.parent)?.kind!==expected)fail("invalid containment: "+e.id+" requires "+expected);
    if(e.kind==="component"&&!e.code.length)fail("source-free component: "+e.id);
  }
  for(const f of snapshot.missingSources)fail("missing source: "+f);
  for(const f of snapshot.sources)if(!owners.has(f))fail("unowned scanned source: "+f);
  const scanned=new Set(snapshot.sources);
  for(const f of owners.keys())if(!scanned.has(f))fail("owned source missing scanner evidence: "+f);
  for(const [metric,expected] of Object.entries(baseline.coverage??{}))
    if((snapshot.coverage?.[metric]??-1)<expected)fail("scanner evidence decreased: "+metric+"; review changed coverage before updating baseline");
  const exceptions=baseline.exceptions??{danglingRows:[],islands:[]};
  for(const x of [...exceptions.danglingRows,...exceptions.islands])
    if(!x.reason?.trim()||!x.evidence?.trim())fail("exception lacks rationale/evidence: "+JSON.stringify(x));
  const endpoint=value=>byId.has(value)?value:owners.get(value);
  const linked=new Set(),pairs=new Set();
  for(const r of snapshot.rows) {
    const a=endpoint(r.source),b=endpoint(r.target);
    const key=r.source+" -> "+r.target;
    const accepted=exceptions.danglingRows.find(x=>x.source===r.source&&x.target===r.target);
    if(!a||!b) {
      if(accepted&&r.authored&&r.status==="stable")warnings.push("accepted stale row: "+key+"; "+accepted.reason);
      else fail("dangling endpoint: "+key);
      continue;
    }
    if(accepted)fail("stale exception is no longer dangling: "+key+"; remove or review the exception");
    if(r.authored&&(!r.description?.trim()||!r.technology?.trim()))fail("undocumented authored relationship: "+key);
    if(r.status==="stable") {linked.add(a);linked.add(b);pairs.add(a+" -> "+b);}
  }
  for(const x of exceptions.danglingRows) {
    if(!snapshot.rows.some(r=>r.source===x.source&&r.target===x.target))fail("obsolete stale-row exception: "+x.source+" -> "+x.target);
    for(const end of [x.source,x.target])
      if(snapshot.missingSources.includes(end)&&owners.has(end))fail("accepted deleted endpoint regained ownership: "+end);
  }
  for(const e of snapshot.elements)if(e.kind==="component"&&!linked.has(e.id)) {
    const exception=exceptions.islands.find(x=>x.id===e.id);
    if(exception?.reason&&exception.evidence)warnings.push("accepted isolated implementation detail: "+e.id);
    else fail("island: "+e.id+"; review actual callers/composition or justify ownership");
  }
  for(const flow of snapshot.flows) {
    if(!flow.title?.trim()||!flow.overview?.trim()||!flow.steps.length)fail("undocumented/empty flow: "+flow.id);
    for(const step of flow.steps) {
      for(const end of [step.source,step.target])if(!byId.has(end))fail("flow "+flow.id+": unknown endpoint "+end);
      if(!pairs.has(step.source+" -> "+step.target))fail("flow "+flow.id+": missing directed collaboration "+step.source+" -> "+step.target);
    }
  }
  for(const task of snapshot.tasks)for(const ref of task.refs) {
    const explicit=ref.startsWith("groma:");
    const value=explicit?ref.slice(6):ref;
    const contextual=/^(https?:|backlog\/|#)/.test(value);
    if(!contextual&&(explicit||task.architecture)&&!byId.has(value)&&!owners.has(value))
      fail("task "+task.id+": unknown reference "+value);
  }
  if(JSON.stringify(semantic(snapshot))!==JSON.stringify(baseline.architecture))
    fail("reviewed architecture changed: inspect element identity/ownership/containment, authored relationships and flows; update baseline only after review");
  const current=new Set(snapshot.duplicates.map(duplicateIdentity));
  for(const id of current) {
    if(!baseline.duplicates.includes(id))fail("unreviewed duplicate: "+id);
    const review=baseline.duplicateReviews[id];
    if(!review?.rationale?.trim()||!review.evidence?.trim()||review.rationale.includes("REVIEW REQUIRED"))
      fail("duplicate lacks review: "+id);
  }
  for(const id of baseline.duplicates)if(!current.has(id))warnings.push("reviewed duplicate resolved/changed: "+id+"; review baseline cleanup");
  const diagnostics=new Map(baseline.diagnostics.map(d=>[[d.scanner,d.code,d.severity].join(":"),d]));
  if(!snapshot.diagnostics.length&&baseline.diagnostics.length)fail("missing scanner diagnostics: fresh evidence required");
  for(const d of snapshot.diagnostics) {
    const key=[d.scanner,d.code,d.severity].join(":");
    if(!diagnostics.has(key)||d.count>diagnostics.get(key).count||(d.facts??0)>(diagnostics.get(key)?.facts??0))fail("scanner degradation: "+key+" count "+d.count+" unresolved/deferred facts "+(d.facts??0));
    else warnings.push("known scanner limitation: "+key+" count "+d.count);
  }
  return {errors,warnings,counts:{components:snapshot.elements.filter(e=>e.kind==="component").length,
    ownedSources:owners.size,flows:snapshot.flows.length,duplicateGroups:current.size}};
}
