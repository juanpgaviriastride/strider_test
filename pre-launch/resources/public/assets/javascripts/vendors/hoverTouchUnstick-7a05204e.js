function hoverTouchUnstick(){if("ontouchstart"in document.documentElement)for(var t=document.styleSheets.length-1;t>=0;t--){var e=document.styleSheets[t];if(e.cssRules)for(var i=e.cssRules.length-1;i>=0;i--){var n=e.cssRules[i];n.selectorText&&(n.selectorText=n.selectorText.replace(":hover",":active"))}}}