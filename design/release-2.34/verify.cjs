/* Browser acceptance checks for the design study. No production endpoints or data. */
const { chromium } = require(process.env.PLAYWRIGHT_MODULE || 'playwright');
const fs = require('node:fs');
const path = require('node:path');
const assert = require('node:assert/strict');
const root = __dirname;
const base = process.env.PROTOTYPE_URL || 'http://127.0.0.1:8234';
const output = process.env.DESIGN_OUTPUT || '/private/tmp/wacken-design-201';
const styles = ['iron', 'field', 'pulse', 'rivet', 'backstage'];
const screens = ['bands', 'detail', 'schedule', 'settings', 'review'];
const results = [];
fs.mkdirSync(output, {recursive:true});
(async () => {
  assert(fs.existsSync(path.join(root,'index.html')), 'Prototype entry point must exist');
  const browser = await chromium.launch({headless:true, executablePath:process.env.CHROMIUM_PATH});
  const page = await browser.newPage({viewport:{width:1240,height:1100},deviceScaleFactor:1});
  const errors = [];
  page.on('pageerror', error => errors.push(error.message));
  async function go(style,screen='bands',extra='') {
    await page.goto(base+'/index.html?style='+style+'&screen='+screen+extra);
    await page.locator('#phone-main').waitFor();
  }
  for(const style of styles) {
    await go(style);
    await page.getByRole('button',{name:'Open Abbie Falls',exact:true}).click();
    await page.getByRole('radio',{name:'4 stars — strong choice',exact:true}).check();
    await assert.equal(await page.locator('#planning-value').textContent(),'4 · Strong choice');
    await page.getByRole('button',{name:'Clear planning rating',exact:true}).click();
    await assert.equal(await page.locator('#planning-value').textContent(),'Unrated');
    assert.equal(await page.getByRole('button',{name:'Open YouTube',exact:true}).count(),0);
    await page.getByRole('button',{name:'Find music links',exact:true}).click();
    await page.getByRole('button',{name:'Search this band',exact:true}).click();
    assert(await page.getByRole('button',{name:'Save approved links',exact:true}).isDisabled());
    await page.getByRole('button',{name:'Inspect evidence',exact:true}).click();
    await page.getByRole('button',{name:'Return to review',exact:true}).click();
    await page.getByRole('checkbox',{name:'I checked the sample identity evidence',exact:true}).check();
    await page.getByRole('button',{name:'Save approved links',exact:true}).click();
    await page.getByRole('status').filter({hasText:'Approved links saved'}).waitFor();
    await page.getByRole('button',{name:'Back to band',exact:true}).click();
    assert.equal(await page.getByRole('button',{name:'Open YouTube',exact:true}).count(),1);
    results.push({style,test:'rate, clear, unknown link, review gate, approve, return',passed:true});

    await go(style,'settings');
    await page.getByRole('button',{name:'Find festival music links',exact:true}).click();
    await assert.equal(await page.locator('#lookup-scope').textContent(),'Festival · Summer Breeze 2027');
    await page.getByRole('button',{name:'Search festival bands',exact:true}).click();
    await page.locator('[data-candidate]').first().waitFor();
    assert.equal(await page.locator('[data-candidate]').count(),2);
    await page.getByRole('button',{name:'Reject all candidates',exact:true}).click();
    await page.getByRole('status').filter({hasText:'No links changed'}).waitFor();
    results.push({style,test:'festival scope and reject',passed:true});

    await go(style,'review','&candidate=ambiguous');
    await page.getByRole('button',{name:'Search this band',exact:true}).click();
    assert(await page.getByRole('button',{name:'Save approved links',exact:true}).isDisabled());
    assert.equal(await page.getByRole('button',{name:'Inspect evidence',exact:true}).count(),0);
    await go(style,'review','&candidate=none');
    await page.getByRole('button',{name:'Search this band',exact:true}).click();
    await page.getByText('No verified match. Links stay empty.',{exact:true}).waitFor();
    results.push({style,test:'ambiguous and no-match never save',passed:true});

    await go(style,'bands','&state=offline');
    await page.getByRole('button',{name:'Open Abbie Falls',exact:true}).click();
    await page.getByRole('radio',{name:'5 stars — must see',exact:true}).check();
    assert((await page.locator('#sync-status').textContent()).includes('pending'));
    await page.getByRole('button',{name:'Find music links',exact:true}).click();
    assert(await page.getByRole('button',{name:'Search this band',exact:true}).isDisabled());
    results.push({style,test:'offline local rating and lookup disabled',passed:true});

    await go(style,'schedule');
    await page.getByRole('button',{name:'Explain schedule symbols',exact:true}).click();
    await page.getByText('Locked = a shared group choice.',{exact:true}).waitFor();
    await page.getByRole('button',{name:'Close legend',exact:true}).click();
    await page.getByRole('button',{name:'Show decision for Electric Callboy',exact:true}).click();
    await page.getByRole('button',{name:'Keep current choice',exact:true}).click();
    results.push({style,test:'schedule legend and decision recovery',passed:true});

    for(const screen of screens) {
      await go(style,screen);
      await page.locator('#phone').screenshot({path:path.join(output,style+'-'+screen+'.png')});
    }
    const variants = [320,390];
    for(const width of variants) for(const scale of [100,200]) for(const screen of screens) {
      await page.setViewportSize({width,height:1000});
      await go(style,screen,'&scale='+scale);
      const geometry = await page.evaluate(() => {
        const p=document.querySelector('#phone'), m=document.querySelector('#phone-main');
        const bounds=p.getBoundingClientRect();
        const targets=[...p.querySelectorAll('button,input:not([type=radio]):not([type=checkbox]),select,.rating-label,.check-label')].filter(e=>e.getBoundingClientRect().width && !e.closest('[hidden]'));
        return {
          pageOverflow:document.documentElement.scrollWidth>innerWidth+1,
          phoneOverflow:p.scrollWidth>p.clientWidth+1,
          contentOverflow:m.scrollWidth>m.clientWidth+1,
          badTargets:targets.filter(e=>{const r=e.getBoundingClientRect();return r.width<47.9||r.height<47.9}).map(e=>e.textContent||e.getAttribute('aria-label')),
          clippedText:[...m.querySelectorAll('h2,h3,p,.band-name,button')].filter(e=>e.scrollWidth>e.clientWidth+2).map(e=>e.textContent)
        };
      });
      assert(!geometry.pageOverflow && !geometry.phoneOverflow && !geometry.contentOverflow,JSON.stringify({style,screen,width,scale,geometry}));
      assert.equal(geometry.badTargets.length,0,JSON.stringify({style,screen,width,scale,geometry}));
      assert.equal(geometry.clippedText.length,0,JSON.stringify({style,screen,width,scale,geometry}));
      results.push({style,test:'layout and targets',screen,width,scale,passed:true});
    }
    await page.setViewportSize({width:1240,height:1100});
    await go(style,'detail');
    const before = await page.locator('#planning-value').textContent();
    await page.locator('#planning-3').focus();
    await page.keyboard.press('Space');
    assert.notEqual(await page.locator('#planning-value').textContent(),before);
    await page.getByRole('button',{name:'Find music links',exact:true}).focus();
    const focus = await page.evaluate(()=>{const e=document.activeElement,s=getComputedStyle(e);return {outline:s.outlineStyle,width:s.outlineWidth}});
    assert(focus.outline!=='none' && parseFloat(focus.width)>=2);
    results.push({style,test:'keyboard rating and visible focus',passed:true});
  }
  await page.setViewportSize({width:2100,height:1150});
  await page.goto(base+'/compare.html');
  await page.waitForFunction(()=>[...document.querySelectorAll('iframe')].every(f=>f.contentDocument?.querySelector('#phone-main')));
  await page.screenshot({path:path.join(output,'comparison.png'),fullPage:true});
  assert.equal(errors.length,0,errors.join('\n'));
  fs.writeFileSync(path.join(output,'browser-results.json'),JSON.stringify({checks:results.length,errors,results},null,2));
  await browser.close();
  console.log('Design checks passed: '+results.length+'; 25 screen captures and comparison saved to '+output);
})().catch(error=>{console.error(error);process.exitCode=1});
