if(!self.define){let e,c={};const i=(i,s)=>(i=new URL(i+".js",s).href,c[i]||new Promise((c=>{if("document"in self){const e=document.createElement("script");e.src=i,e.onload=c,document.head.appendChild(e)}else e=i,importScripts(i),c()})).then((()=>{let e=c[i];if(!e)throw new Error(`Module ${i} didn’t register its module`);return e})));self.define=(s,r)=>{const d=e||("document"in self?document.currentScript.src:"")||location.href;if(c[d])return;let n={};const f=e=>i(e,d),o={module:{uri:d},exports:n,require:f};c[d]=Promise.all(s.map((e=>o[e]||f(e)))).then((e=>(r(...e),n)))}}define(["./workbox-6bfbe135"],(function(e){"use strict";self.addEventListener("message",(e=>{e.data&&"SKIP_WAITING"===e.data.type&&self.skipWaiting()})),e.precacheAndRoute([{url:"box-closed.svg",revision:"227cae959bbbd41fa38df30bc6f32aea"},{url:"box-open.svg",revision:"0b6cfd1fbf231393dd54ad19072487c3"},{url:"F.svg",revision:"4d65577a57cd407163dbba539cde6d7b"},{url:"help.css",revision:"94bc37e4c71b7b21248c29ee40bb2308"},{url:"help.html",revision:"99a8f9e6cd88a78ef915a65d571efcd5"},{url:"help.sass",revision:"891c171e0810071b24c5160b89c0d82a"},{url:"icon-192x192.png",revision:"6bde2f3b6b77200b4e9fdd4844e35429"},{url:"icon-256x256.png",revision:"02d79a71b2770dd4b480ba3fbb88338e"},{url:"icon-384x384.png",revision:"7b1c6d2f58b4f33f3c784f10ba67897c"},{url:"icon-512x512.png",revision:"dacf1ca312cd1a165e6fdd9ad9fc1c37"},{url:"icon-maskable.png",revision:"cef3cc7f137dd20e3b3e72f96a22dd6f"},{url:"index.html",revision:"3d6c34539bb0200a9970637daaf0d2ce"},{url:"logo.svg",revision:"3d3aefcdf073cedfecf7daf2b289ccdd"},{url:"manifest.webmanifest",revision:"e67ff83bac1e4ac81e0b73f223d43955"},{url:"style.css",revision:"38535cde21c09e0805e18a0b51f5b8df"},{url:"style.sass",revision:"118b964179fbac994234625c2313b341"},{url:"truck.svg",revision:"90a66740c2512f2b489a9847c8719216"}],{ignoreURLParametersMatching:[/^utm_/,/^fbclid$/]})}));
//# sourceMappingURL=sw.js.map