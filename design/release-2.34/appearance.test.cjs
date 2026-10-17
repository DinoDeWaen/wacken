const {test}=require('node:test');
const assert=require('node:assert/strict');
const {normalizePreference,resolveTheme}=require('./appearance.js');
test('system default follows the device in both directions',()=>{
 assert.equal(resolveTheme('system',true),'iron');
 assert.equal(resolveTheme('system',false),'field');
});
test('an explicit choice overrides opposite device preference',()=>{
 assert.equal(resolveTheme('dark',false),'iron');
 assert.equal(resolveTheme('light',true),'field');
});
test('invalid or missing stored preference safely follows system',()=>{
 for(const value of [undefined,null,'','purple','__proto__']){
  assert.equal(normalizePreference(value),'system');
  assert.equal(resolveTheme(value,false),'field');
 }
});
test('explicit preferences remain stable when device appearance changes',()=>{
 for(const device of [true,false,true]){
  assert.equal(resolveTheme('light',device),'field');
  assert.equal(resolveTheme('dark',device),'iron');
 }
});

