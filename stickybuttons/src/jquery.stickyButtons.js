(function($) {
	var ACCELERATION = p(.8,.8), CONVERT = p(.2,.2), STICK_RATIO = 1/5, DRAG_RATIO = 1/8, Z_INDEX_WHEN_STICK=500;
	
	var STICKY_FINISHED_EVENT = 'sticky_finished';
	
	var preventDrag = false;
	$(document).mousedown(function(e){if(preventDrag)e.preventDefault();})
	function doElement(interval) {
		var ratio = STICK_RATIO;
		var o=$(this), startPos=curPos(o), tmp=bind(o, onMove, p()), bOnMove=function(e){tmp(e,ratio, startPos);};
		var zIndex={bg:bind(o,o.css,'z-index',o.css('z-index')), front:bind(o,o.css,'z-index',Z_INDEX_WHEN_STICK)}
		function stop(resetStartPos){if(resetStartPos){startPos=curPos(o)};interval=clearInterval(interval);}
		function noFollow(e) { 
			if (!interval) {
				preventDrag = false; 
				zIndex.bg().unbind('mousemove', bOnMove)
				interval = setInterval(bind(o, run, getSteps(startPos, curPos(o))), 20);
			}
		}
		o.mouseup(noFollow)
			.mouseleave(noFollow)
			.mouseenter(function(e) {
				stop();
				preventDrag=true; 
				ratio=STICK_RATIO; 
				zIndex.front().mousemove(bOnMove);
			})
			.mousedown(function(e) { 
				ratio=DRAG_RATIO; 
			})
			.bind(STICKY_FINISHED_EVENT, stop)
	}
	$.fn.stickyButton = function() { return this.each(doElement); }
	
	function bind(f) {
		var n=1,a=arguments,c;
		if (typeof(f)!=='function')c=f,f=a[n++];a=[].slice.call(a,n)
		return function(){var args=[].slice.call(arguments);return f.apply(c||this, a.concat(args));}
	}
	function isO(x,p){return x&&'object'===typeof(x)&&(p?x.hasOwnProperty(p):true);}
	function p(x,y){
		if(this===window){return new p(x,y);}
		if(isO(x,'top')){y=x.top;x=x.left;}
		else if(isO(x,'pageX')){y=x.pageY;x=x.pageX;} 
		this.x=x||0;this.y=y||0;
	}
	p.prototype={
		clone:function(){return new p(this.x,this.y);},
		toString:function(){with(this)return "x: "+x+", y: "+y;},
		add:function(x,y){if(isO(x)){y=x.y;x=x.x}this.x+=x;this.y+=y;return this;},
		sub:function(x,y){if(isO(x)){y=x.y;x=x.x}this.x-=x;this.y-=y;return this;},
		mul:function(x,y){if(isO(x)){y=x.y;x=x.x}this.x*=x;this.y*=y;return this;},
		set:function(x,y){if(isO(x)){y=x.y;x=x.x}this.x=x;this.y=y;return this;}
	}
	function getPos(curPos, tPos, target) {
		return tPos.mul(ACCELERATION).add(target.clone().sub(curPos).mul(CONVERT)).clone().add(curPos);
	}
	function setPos(o, pos) {
		return o.css('left', Math.round(pos.x)).css('top', Math.round(pos.y));
	}
	function onMove(tPos, e, ratio, start) {
		var o=this, mouse = p(e).sub(p(o.offset())).add(start);
		var delta = mouse.clone().sub(start).add(o.width()*2, o.height()*2).mul(ratio,ratio);
		setPos(o, getPos(curPos(o), tPos, mouse.sub(delta)));
	}
	function getSteps(target, cur, tPos, S) {
		if (Math.abs(target.x-cur.x) < .1 && Math.abs(target.y-cur.y) < .1)return[target, cur];
		tPos=tPos||p();
		(S = getSteps(target, getPos(cur, tPos, target), tPos)).push(cur)
		return S
	}
	function run(steps) {
		if (steps.length) setPos(this, steps.pop())
		else this.trigger(STICKY_FINISHED_EVENT)
	}
	// this is the place too hook switch btw absolute/relative position
	// instead of .parent() - $(window) should be used to meausure scroll distance
	function curPos(o) {
		return p(o.position()).add(o.parent().scrollLeft(), o.parent().scrollTop());
	}
})(jQuery)