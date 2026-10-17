/* Shared by the HTML study and dependency-free preference tests. */
(function(root){
 'use strict';
 function normalizePreference(value){return ['system','dark','light'].includes(value)?value:'system';}
 function resolveTheme(preference,systemDark){
  const p=normalizePreference(preference);
  return (p==='dark'||(p==='system'&&systemDark))?'iron':'field';
 }
 const api=Object.freeze({normalizePreference,resolveTheme});
 if(typeof module!=='undefined'&&module.exports)module.exports=api;
 else root.Appearance=api;
})(typeof window!=='undefined'?window:globalThis);

