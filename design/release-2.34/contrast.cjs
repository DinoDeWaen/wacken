/* Dependency-free WCAG palette calculation; does not inspect rendered pixels. */
const fs=require('node:fs'),assert=require('node:assert/strict');
const css=fs.readFileSync(__dirname+'/styles.css','utf8');
const blocks=[['iron',css.match(/#phone,dialog\{([^}]+)\}/)[1]],...['field','pulse','rivet','backstage'].map(name=>[name,css.match(new RegExp('dialog\\[data-style='+name+'\\]\\{([^}]+)\\}'))[1]])];
function luminance(hex){
 let c=hex.replace('#','');if(c.length===3)c=[...c].map(x=>x+x).join('');
 const v=[0,2,4].map(i=>parseInt(c.slice(i,i+2),16)/255).map(n=>n<=.04045?n/12.92:((n+.055)/1.055)**2.4);
 return v[0]*.2126+v[1]*.7152+v[2]*.0722;
}
function ratio(a,b){const x=luminance(a),y=luminance(b);return (Math.max(x,y)+.05)/(Math.min(x,y)+.05);}
const rows=[];
for(const [style,block] of blocks){
 const colors=Object.fromEntries([...block.matchAll(/--([a-z-]+):\s*(#[0-9a-f]+)/g)].map(m=>[m[1],m[2]]));
 const checks=[];
 for(const fg of ['text','muted','accent','danger'])for(const bg of ['bg','panel','raised'])checks.push({fg,bg,ratio:ratio(colors[fg],colors[bg]),minimum:4.5});
 checks.push({fg:'on-accent',bg:'accent',ratio:ratio(colors['on-accent'],colors.accent),minimum:4.5});
 for(const bg of ['bg','panel','raised'])checks.push({fg:'line',bg,ratio:ratio(colors.line,colors[bg]),minimum:3});
 rows.push({style,checks});
}
console.log(JSON.stringify({method:'WCAG relative luminance of CSS palette tokens; excludes disabled opacity, compositing and rendered focus geometry',productionWhiteOnRed:ratio('#ffffff','#ff3b6b'),styles:rows},null,2));
for(const row of rows)for(const c of row.checks)assert(c.ratio>=c.minimum,row.style+' '+c.fg+'/'+c.bg+' '+c.ratio.toFixed(2));

