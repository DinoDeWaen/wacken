'use strict';
const styles = {
  iron:{name:'Ironline',note:'Familiar metal, cleaner hierarchy.',detail:'A deliberate evolution: flat list rows, compact structure, amber emphasis and clear labels.',tradeoff:'Lowest visual departure. Larger targets trade a little row density for easier selection.'},
  field:{name:'Field Notes',note:'An editorial festival notebook.',detail:'Warm paper, serif headings, numbered lists and open sections. A light-theme research alternative.',tradeoff:'Requires approval to depart from the current dark-theme requirement. Glare benefit is a hypothesis, not a measured result.'},
  pulse:{name:'Pulse',note:'Friendly cards, confident actions.',detail:'Rounded cards, generous grouping and large controls. A relaxed approach to rating and recovery.',tradeoff:'Easy to distinguish groups; fewer bands fit above the fold. Test with frequent rating users.'},
  rivet:{name:'Rivet',note:'A precise planning instrument.',detail:'Square controls, monospace labels, explicit state and a compact register. Utility comes first.',tradeoff:'Suited to planning/admin work; may feel less like a music companion. Body copy stays proportional.'},
  backstage:{name:'Backstage',note:'A music-led editorial direction.',detail:'Expressive serif titles, a featured band and a two-column band shelf with warm stage-light tones.',tradeoff:'Strongest personality and lowest list density. Cards reflow to one column for narrow screens and large text.'}
};
const fixtureBands = [
  {id:'abbie',name:'Abbie Falls',genre:'Metalcore',rating:0,known:false},
  {id:'given',name:'Any Given Day',genre:'Metalcore',rating:5,known:false},
  {id:'callboy',name:'Electric Callboy',genre:'Electronicore',rating:4,known:true},
  {id:'hammer',name:'HammerFall',genre:'Power metal',rating:3,known:true},
  {id:'lord',name:'Lord Of The Lost',genre:'Dark rock',rating:0,known:false},
  {id:'orchestra',name:'Summer Breeze Legends & Orchestra',genre:'Festival special',rating:0,known:false}
];
const params = new URLSearchParams(location.search);
const allowedScreens = ['bands','detail','schedule','settings','review','festival'];
const state = {
  style:styles[params.get('style')]?params.get('style'):'iron',
  screen:allowedScreens.includes(params.get('screen'))?params.get('screen'):'bands',
  mode:['online','offline','error','loading','empty'].includes(params.get('state'))?params.get('state'):'online',
  scale:params.get('scale')==='200'?'200':'100',
  candidate:['verified','ambiguous','none'].includes(params.get('candidate'))?params.get('candidate'):'verified',
  band:'abbie', scope:'band', query:'', filter:'all', term:'Abbie Falls',
  ratings:Object.fromEntries(fixtureBands.map(b=>[b.id,b.rating])), real:{},
  links:Object.fromEntries(fixtureBands.map(b=>[b.id,b.known])),
  pending:0,dirty:new Set(),message:'',lookupDone:false,lookupRunning:false,
  lookupError:false,evidenceSeen:false,approved:false,searchSeq:0,lock:'callboy',
  festival:'Summer Breeze 2027',noActive:params.get('state')==='empty'
};
const phone = document.getElementById('phone');
const main = document.getElementById('phone-main');
const dialog = document.getElementById('app-dialog');
const esc = value=>String(value).replace(/[&<>"']/g,c=>({'&':'&amp;','<':'&lt;','>':'&gt;','"':'&quot;',"'":'&#39;'}[c]));
const band = ()=>fixtureBands.find(b=>b.id===state.band);
const ratingName = n=>['Unrated','1 · Veto','2 · Low interest','3 · Optional','4 · Strong choice','5 · Must see'][n];
const button = (label,action,klass='',attrs='')=>'<button type="button" class="'+klass+'" data-action="'+action+'" '+attrs+'>'+label+'</button>';
const flash = ()=>state.message?'<p id="live-message" role="status" aria-live="polite" class="flash">'+esc(state.message)+'</p>':'<p id="live-message" role="status" aria-live="polite"></p>';
const offline = ()=>state.mode==='offline';
function updateUrl(){
  const p=new URLSearchParams({style:state.style,screen:state.screen,state:state.mode,scale:state.scale,candidate:state.candidate});
  if(params.get('embed')==='1')p.set('embed','1');
  history.replaceState(null,'','?'+p.toString());
}
function setScreen(screen){
  state.screen=screen;state.message='';state.searchSeq++;state.lookupRunning=false;
  if(screen==='review'){state.term=state.scope==='festival'?'':band().name;state.lookupDone=false;state.evidenceSeen=false;state.approved=false;state.lookupError=false;}
  render(); main.scrollTop=0;main.focus({preventScroll:true});
}
function syncStatus(){
  const pending=state.pending+(offline()?2:0);
  if(offline())return 'Offline · available on this device · '+pending+' changes pending';
  if(state.mode==='error')return 'Sync needs attention · changes are safe on this device';
  if(state.mode==='loading')return 'Refreshing · cached data is ready to use';
  return pending?'Saved on this device · '+pending+' changes pending':'Up to date · all changes saved';
}
function render(){
  phone.dataset.style=state.style;phone.dataset.scale=state.scale;
  dialog.dataset.style=state.style;dialog.style.fontSize=state.scale==='200'?'32px':'16px';
  document.querySelectorAll('[data-style-choice]').forEach(b=>b.setAttribute('aria-pressed',String(b.dataset.styleChoice===state.style)));
  document.getElementById('style-caption').innerHTML='<strong>'+styles[state.style].name+'</strong> · '+styles[state.style].note;
  document.getElementById('screen-select').value=['festival'].includes(state.screen)?'bands':state.screen;
  document.getElementById('state-select').value=state.mode;
  document.getElementById('scale-select').value=state.scale;
  document.getElementById('candidate-select').value=state.candidate;
  document.getElementById('phone-header').innerHTML='<div class="app-bar"><span class="app-brand">'+(state.style==='rivet'?'W/P — FIELD SYSTEM':'Wacken / Planner')+'</span>'+button('↻','sync','quiet','aria-label="Sync now"')+'</div><p class="festival-label">'+(state.noActive?'No active festival':esc(state.festival))+'</p>';
  document.getElementById('sync-status').innerHTML='<span class="sync-dot" aria-hidden="true"></span><span>'+syncStatus()+'</span>'+(state.mode==='error'?button('Retry','sync','quiet','aria-label="Retry sync"'):'');
  const views={bands:bandsView,detail:detailView,schedule:scheduleView,settings:settingsView,review:reviewView,festival:festivalView};
  main.innerHTML=views[state.screen]();
  const current=['detail','festival'].includes(state.screen)?'bands':state.screen==='review'?(state.scope==='festival'?'settings':'bands'):state.screen;
  document.getElementById('phone-nav').innerHTML=[['bands','Bands','≡'],['schedule','Schedule','▦'],['settings','Settings','⚙']].map(([id,label,icon])=>button('<span class="nav-symbol" aria-hidden="true">'+icon+'</span>'+label,'nav-'+id,'',current===id?'aria-current="page"':'')).join('');
  updateUrl();
}
function bandsView(){
  if(state.noActive)return '<p class="eyebrow">Between festivals</p><h2>What’s next?</h2><p class="muted">Your previous festivals and personal ratings are still here.</p>'+button('Add festival','add-festival','primary block')+'<section class="section"><h3>Festival archive</h3><p>Wacken Open Air 2026</p><p class="small muted">Archived · ratings are read-only</p>'+button('View archive example','archive-example','block')+'</section>'+flash();
  const titles={iron:'The line-up',field:'Bands, worth hearing.',pulse:'Find your next favourite.',rivet:'Band register',backstage:'Turn it up.'};
  return '<p class="eyebrow">'+(state.style==='field'?'Your festival notebook':'Summer Breeze / 2027')+'</p><h2>'+titles[state.style]+'</h2><p class="muted small">63 bands · six-band study sample</p><label for="band-search" class="search-label">Find a band</label><input id="band-search" type="search" placeholder="Search the line-up" value="'+esc(state.query)+'"><div class="filter-row" aria-label="Band filters">'+button('All bands','filter-all','', 'aria-pressed="'+(state.filter==='all')+'"')+button('Unrated','filter-unrated','','aria-pressed="'+(state.filter==='unrated')+'"')+'</div><div id="band-results">'+bandRows()+'</div>'+flash();
}
function bandRows(){
  const list=fixtureBands.filter(b=>b.name.toLowerCase().includes(state.query.toLowerCase())&&(state.filter!=='unrated'||state.ratings[b.id]===0));
  if(!list.length)return '<section class="section"><h3>No bands found</h3><p class="muted">Try a different name or show all bands.</p>'+button('Clear search and filters','clear-search','block')+'</section>';
  return '<div class="band-list">'+list.map((b,i)=>'<button type="button" class="band-row" data-action="band" data-band="'+b.id+'" aria-label="Open '+esc(b.name)+'"><span class="band-order" aria-hidden="true">'+String(i+1).padStart(2,'0')+'</span><span><strong class="band-name">'+esc(b.name)+'</strong><span class="band-meta">'+b.genre+' · '+(state.links[b.id]?'Music links available':'Music links not added')+'</span></span><span class="band-score">'+(state.ratings[b.id]?state.ratings[b.id]+' <small>★</small>':'—')+'</span></button>').join('')+'</div>';
}
function ratingControl(kind,value){
  const labels=['veto','low interest','optional','strong choice','must see'];
  return '<fieldset class="rating-fieldset"><legend>'+(kind==='planning'?'Your planning rating':'Your post-show rating')+'</legend><div class="ratings">'+labels.map((label,i)=>'<label class="rating-label"><input id="'+kind+'-'+(i+1)+'" type="radio" name="'+kind+'" value="'+(i+1)+'" aria-label="'+(i+1)+' stars — '+label+'" '+(value===i+1?'checked':'')+'><span aria-hidden="true">'+(value>=i+1?'★':'☆')+'</span></label>').join('')+'</div><p id="'+(kind==='planning'?'planning-value':'real-value')+'">'+ratingName(value)+'</p>'+button('Clear rating','clear-'+kind,'quiet','aria-label="Clear '+(kind==='planning'?'planning':'post-show')+' rating"')+'</fieldset>';
}
function detailView(){
  const b=band(),value=state.ratings[b.id],known=state.links[b.id];
  return button('← Bands','back-bands','quiet')+'<p class="eyebrow">'+b.genre+'</p><h2>'+esc(b.name)+'</h2><p class="muted">Make this line-up yours.</p><section class="section">'+ratingControl('planning',value)+'<p class="small muted">Planning helps your group choose what to see. 1 = veto, 5 = must see.</p></section><section class="section"><h3 class="section-heading">Your group</h3><p>Dino 4 ★ · Sofie 5 ★</p><p class="small muted">Example shared ratings · read-only</p></section><section class="section"><h3 class="section-heading">Band links</h3>'+(known?'<div class="link-row">'+button('YouTube ↗','external-youtube','','aria-label="Open YouTube"')+button('Spotify ↗','external-spotify','','aria-label="Open Spotify"')+'</div>':'<p class="muted">No verified music links added.</p>')+button('Find music links','find-band','primary block')+'</section><section class="section"><h3>Running order</h3><p class="muted">Stage and time to be announced.</p></section><details class="section"><summary>After the show</summary><p class="small muted">This personal rating is separate from planning and does not change the group schedule.</p>'+ratingControl('real',state.real[b.id]||0)+'</details>'+flash();
}
function scheduleView(){
  if(state.noActive)return '<h2>No active schedule</h2><p>Your archived schedules remain available with their festivals.</p>'+button('Go to bands','back-bands','primary');
  const selected=state.lock==='callboy'?'Electric Callboy':'HammerFall';
  return '<p class="eyebrow">An evening together</p><h2>The schedule</h2><p class="small muted">Illustrative times and stages · not a published festival timetable</p><div class="button-row">'+button('Day 1','day','primary','aria-pressed="true"')+button('Legend','legend','','aria-label="Explain schedule symbols"')+'</div><p class="small"><strong>Current group choice:</strong> '+selected+'</p><div class="timeline-scroll" tabindex="0" aria-label="Evening timetable, scroll horizontally"><table class="timeline"><caption class="small">Example evening · stage labels stay visible</caption><thead><tr><th scope="col">Stage</th><th scope="col">18:00</th><th scope="col">19:00</th><th scope="col">20:00</th></tr></thead><tbody><tr><th scope="row">Main Stage</th><td>—</td><td>—</td><td>'+button('<strong>20:00–21:00</strong><strong>Electric Callboy</strong><span>★ 5 · '+(state.lock==='callboy'?'Locked choice':'Alternative')+'</span>','decision','set-block','aria-label="Show decision for Electric Callboy"')+'</td></tr><tr><th scope="row">T-Stage</th><td>—</td><td>—</td><td>'+button('<strong>20:15–21:15</strong><strong>HammerFall</strong><span>★ 3 · '+(state.lock==='callboy'?'Overlap · skip':'Locked choice')+'</span>','decision','set-block '+(state.lock==='callboy'?'skipped':''),'aria-label="Show decision for HammerFall"')+'</td></tr></tbody></table></div><p class="legend-summary">Locked = your group’s choice. Dashed = a skipped overlap. Tap a set to review alternatives.</p><section class="section"><h3>Plan without losing your place</h3><p class="muted">Ratings and shared choices stay separate. Changing a choice does not rewrite a rating.</p></section>'+flash();
}
function settingsView(){
  return '<p class="eyebrow">Your festival, together</p><h2>Settings</h2><section class="section"><h3>Sync & offline</h3><p>'+syncStatus()+'</p>'+button('Sync now','sync','primary block')+'<p class="small muted">Cached bands and your saved ratings remain available if the network drops.</p></section><section class="section"><h3>Festival administration</h3><p class="small muted">'+esc(state.festival)+'</p>'+button('Link bands','link-bands','block')+button('Find music links','find-festival','block','aria-label="Find festival music links"')+button('Import line-up','import-info','block')+'<details class="details-row"><summary>Festival management</summary>'+button('Archive festival','archive','danger block',offline()?'disabled':'')+'<p class="small muted">Archiving keeps past ratings and history.</p></details></section><section class="section"><h3>Your group</h3><p>Sofie and Dino</p><p class="small muted">Single shared planning group</p>'+button('Share group invite','invite','block')+'</section><section class="section"><h3>Your ratings</h3><p class="muted">Your personal archive travels with you.</p>'+button('Export ratings CSV','export','block')+'</section>'+flash();
}
function reviewView(){
  const festival=state.scope==='festival',disabled=offline()||state.lookupRunning||state.noActive;
  const scope=festival?'Festival · '+state.festival:'Band · '+band().name;
  let output=button(festival?'← Settings':'← Band',festival?'back-settings':'back-band','quiet')+'<p class="eyebrow">Administration / music links</p><h2>Find the right band.</h2><p id="lookup-scope" class="scope">'+esc(scope)+'</p><p class="small muted">Check who a channel belongs to before adding it. Existing links are kept.</p><form id="lookup-form"><label for="lookup-term" class="search-label">'+(festival?'Name filter (optional)':'Search name')+'</label><input id="lookup-term" type="text" value="'+esc(state.term)+'" placeholder="'+(festival?'All missing music links':'Band name')+'"><button type="submit" class="primary block" '+(disabled?'disabled':'')+'>'+(festival?'Search festival bands':'Search this band')+'</button></form>';
  if(offline())output+='<p class="flash">Connect to search verified sources. Existing cached links are unchanged.</p>';
  if(state.lookupRunning)output+='<p role="status" class="flash">Checking source evidence for '+(festival?'two sample bands':'this band')+'…</p>'+button('Cancel search','cancel-lookup','block');
  if(state.lookupError)output+='<p role="alert" class="flash error">Could not check sources. No links changed. Retry when you are ready.</p>';
  if(state.lookupDone && !state.lookupError){
    if(state.candidate==='none')output+='<section class="section"><h3>No result</h3><p>No verified match. Links stay empty.</p></section>';
    else {
      const names=festival?['Abbie Falls','Any Given Day']:[band().name];
      output+=names.map(name=>'<section class="section" data-candidate><span class="tag">'+(state.candidate==='verified'?'Verified example · simulated':'Identity not verified')+'</span><h3>'+esc(name)+'</h3><p class="small muted">YouTube channel + Spotify artist profile</p>'+(state.candidate==='verified'?'<p class="small">youtube.com/channel/EXAMPLE_ONLY<br>open.spotify.com/artist/EXAMPLE_ONLY</p><p class="small muted">Sample identity evidence, not a real lookup.</p>'+button('Inspect evidence','evidence','block'):'<p>More than one artist uses this name. A name match does not establish identity.</p><p class="small muted">Refine the search or leave these fields empty.</p>')+'</section>').join('');
    }
    if(state.candidate==='verified'&&state.evidenceSeen)output+='<div class="evidence"><h3>Sample evidence checked</h3><p class="small">An example official-artist page links to both canonical profiles; the same band identity is corroborated by an example music-identity record.</p><p class="small muted">The real feature must fetch and cite actual sources before allowing approval.</p></div><label class="check-label"><input type="checkbox" id="approve-evidence" '+(state.approved?'checked':'')+'> <span>I checked the sample identity evidence</span></label>';
    output+='<div class="button-row">'+button('Reject all candidates','reject')+'</div>';
  }
  output+=button('Save approved links','save-links','primary block',!(state.lookupDone&&state.candidate==='verified'&&state.evidenceSeen&&state.approved&&!offline())?'disabled':'');
  return output+flash();
}
function festivalView(){
  return button('← Bands','back-bands','quiet')+'<p class="eyebrow">A new chapter</p><h2>Add festival</h2><p class="muted">Your band history stays with you.</p><form id="festival-form"><label for="festival-name" class="search-label">Festival name</label><input id="festival-name" type="text" required placeholder="For example, Summer Breeze 2027"><label for="festival-file" class="search-label">Bands CSV</label><input id="festival-file" type="file" accept=".csv,text/csv" required><p class="small muted">Use a CSV with a name column. This study only previews the selection, without reading or uploading the file.</p><button type="submit" class="primary block" '+(offline()?'disabled':'')+'>Create festival example</button></form>'+flash();
}
function showDialog(title,content,actions){
  document.getElementById('dialog-content').innerHTML='<h2 id="dialog-title">'+title+'</h2>'+content+'<div class="button-row">'+actions+'</div>';
  dialog.showModal();
}
function closeDialog(){dialog.close()}
function handleAction(action,target){
  if(action.startsWith('nav-')){setScreen(action.slice(4));return;}
  if(action==='band'){state.band=target.dataset.band;setScreen('detail');return;}
  if(action==='back-bands'){setScreen('bands');return;}
  if(action==='back-band'){setScreen('detail');return;}
  if(action==='back-settings'){setScreen('settings');return;}
  if(action.startsWith('filter-')){state.filter=action.slice(7);render();return;}
  if(action==='clear-search'){state.filter='all';state.query='';render();return;}
  if(action.startsWith('clear-')){
    const kind=action.slice(6);if(kind==='planning')state.ratings[state.band]=0;else state.real[state.band]=0;
    markSaved(kind);render();return;
  }
  if(action==='find-band'||action==='find-festival'){state.scope=action==='find-band'?'band':'festival';setScreen('review');return;}
  if(action==='cancel-lookup'){state.searchSeq++;state.lookupRunning=false;state.message='Search cancelled. No links changed.';render();return;}
  if(action==='evidence'){
    state.evidenceSeen=true;
    showDialog('Inspect the sample evidence','<p>This is a verification scenario for design review. No real artist profiles have been researched here.</p><p><strong>Example artist website:</strong><br>official-band.example/music</p><p><strong>Example identity cross-check:</strong><br>music-identity.example/artist</p><p>In the real feature, these would be fetched source links showing the canonical YouTube and Spotify profiles and enough identity detail to reject namesakes.</p>',button('Return to review','close-evidence','primary'));
    render();return;
  }
  if(action==='close-evidence'){closeDialog();render();return;}
  if(action==='save-links'){
    if(!state.approved||!state.evidenceSeen||state.candidate!=='verified'||offline())return;
    if(state.scope==='festival'){state.links.abbie=true;state.links.given=true;}
    else state.links[state.band]=true;
    state.message='Approved links saved in this example. Known links were kept.';
    state.lookupDone=false;state.approved=false;render();return;
  }
  if(action==='reject'){state.lookupDone=false;state.approved=false;state.message='No links changed. Missing fields stay empty.';render();return;}
  if(action==='legend'){showDialog('Read the schedule','<p>Locked = a shared group choice.</p><p>Dashed / skip = an overlap the group is not choosing. A star count and text give meaning without relying only on color.</p><p>5 = must see, 4 = strong choice, 2–3 = optional, 1 = veto. Equal ratings are not automatically marked as skipped.</p>',button('Close legend','close-dialog','primary'));return;}
  if(action==='decision'){showDialog('One evening, two stages','<p><strong>Electric Callboy</strong><br>20:00–21:00 · Main Stage · example group choice</p><p><strong>HammerFall</strong><br>20:15–21:15 · T-Stage · overlapping alternative</p><p class="muted">Illustrative times · 15-minute example walk. Changing a shared choice keeps personal ratings unchanged.</p>',button('Keep current choice','close-dialog')+button('Choose the alternative','alternative','primary'));return;}
  if(action==='alternative'){state.lock=state.lock==='callboy'?'hammer':'callboy';closeDialog();state.message='Shared choice updated in this example. Ratings unchanged.';render();return;}
  if(action==='close-dialog'){closeDialog();return;}
  if(action==='sync'){
    if(offline()){state.message='You are offline. Your changes are safe on this device.';render();return;}
    state.mode='online';state.pending=0;state.dirty.clear();state.message='Sync complete in this example.';render();return;
  }
  if(action==='archive'){showDialog('Archive this festival?','<p>'+esc(state.festival)+' will become read-only. Your bands and personal rating history are kept.</p>',button('Keep festival','close-dialog')+button('Archive festival','confirm-archive','primary'));return;}
  if(action==='confirm-archive'){closeDialog();state.noActive=true;setScreen('bands');return;}
  if(action==='add-festival'){setScreen('festival');return;}
  if(action==='archive-example'){showDialog('Wacken Open Air 2026','<p>Archived festival example. Historical planning and post-show ratings stay readable; controls are read-only.</p>',button('Close archive','close-dialog','primary'));return;}
  if(action==='link-bands'){showDialog('Review band identity','<p>Any Given Day → Any given Day</p><p>This is a proposed link to the existing golden-source band. Review the identity before reusing its metadata and history.</p>',button('No match / keep separate','close-dialog')+button('Confirm example link','confirm-link','primary'));return;}
  if(action==='confirm-link'){closeDialog();state.message='Band link confirmed in this example. Personal history was kept.';render();return;}
  const messages={'external-youtube':'YouTube hand-off preview. No external app or website opened.','external-spotify':'Spotify hand-off preview. No external app or website opened.','import-info':'Import preview: choose and validate files before any shared update.','invite':'Invite preview: no message sent.','export':'Export preview: no real ratings exported.','day':'Day 1 selected in this example.'};
  if(messages[action]){state.message=messages[action];render();}
}
function markSaved(kind){
  if(offline()||state.mode==='error'){state.dirty.add(state.band+kind);state.pending=state.dirty.size;}
  state.message=(kind==='planning'?'Planning':'Post-show')+' rating saved on this device'+(offline()?' · sync pending.':'.');
}
document.addEventListener('click',event=>{
  const target=event.target.closest('[data-action]');
  if(target){handleAction(target.dataset.action,target);return;}
  const choice=event.target.closest('[data-style-choice]');
  if(choice){state.style=choice.dataset.styleChoice;render();}
});
main.addEventListener('input',event=>{
  if(event.target.id==='band-search'){state.query=event.target.value;document.getElementById('band-results').innerHTML=bandRows();}
});
main.addEventListener('change',event=>{
  const input=event.target;
  if(input.name==='planning'||input.name==='real'){
    const kind=input.name;if(kind==='planning')state.ratings[state.band]=+input.value;else state.real[state.band]=+input.value;
    markSaved(kind);const id=input.id;render();document.getElementById(id)?.focus({preventScroll:true});
  }
  if(input.id==='approve-evidence'){state.approved=input.checked;render();document.getElementById('approve-evidence')?.focus({preventScroll:true});}
});
main.addEventListener('submit',event=>{
  event.preventDefault();
  if(event.target.id==='lookup-form'){
    state.term=document.getElementById('lookup-term').value;
    if(offline())return;
    state.lookupRunning=true;state.lookupDone=false;state.lookupError=false;state.approved=false;state.evidenceSeen=false;state.message='';
    const seq=++state.searchSeq;render();
    setTimeout(()=>{if(seq!==state.searchSeq)return;state.lookupRunning=false;state.lookupDone=true;state.lookupError=state.mode==='error';render();},250);
  }
  if(event.target.id==='festival-form'){
    const name=document.getElementById('festival-name').value.trim();
    if(!name||!document.getElementById('festival-file').files.length)return;
    state.festival=name;state.noActive=false;state.mode='online';setScreen('bands');state.message='Festival example created. No file was uploaded.';render();
  }
});
document.getElementById('style-options').innerHTML=Object.entries(styles).map(([id,s],i)=>'<button type="button" class="style-choice" data-style-choice="'+id+'" aria-pressed="'+(id===state.style)+'"><strong>0'+(i+1)+' / '+s.name+'</strong><span>'+s.note+'</span></button>').join('');
document.getElementById('screen-select').addEventListener('change',event=>{if(event.target.value==='review')state.scope='band';setScreen(event.target.value)});
document.getElementById('state-select').addEventListener('change',event=>{state.mode=event.target.value;state.noActive=state.mode==='empty';state.message='';render()});
document.getElementById('scale-select').addEventListener('change',event=>{state.scale=event.target.value;render()});
document.getElementById('candidate-select').addEventListener('change',event=>{state.candidate=event.target.value;state.lookupDone=false;state.approved=false;state.evidenceSeen=false;render()});
document.getElementById('reset-study').addEventListener('click',()=>{location.href='?style='+state.style});
document.getElementById('download-review').addEventListener('click',()=>{
  const text='# Wacken Planner 2.34 design review\n\nPreferred direction: '+(document.getElementById('preferred-style').value||'Undecided')+'\n\n'+document.getElementById('review-notes').value+'\n\nSelf-reported user feedback; not an automated usability score.\n';
  const url=URL.createObjectURL(new Blob([text],{type:'text/markdown'}));
  const a=document.createElement('a');a.href=url;a.download='wacken-2.34-design-review.md';a.click();setTimeout(()=>URL.revokeObjectURL(url),1000);
  document.getElementById('review-status').textContent='Review downloaded. Share it in chat when ready; nothing was sent automatically.';
});
if(params.get('embed')==='1')document.body.classList.add('embed');
window.study={styles,state,render};
render();

