/*
 * Copyright (c) 2010-2011 Marek Bedkowski. All rights reserved.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
(function($) {
	var ACCELERATION = p(.8,.8), CONVERT = p(.2,.2), STICK_RATIO = 1/5, DRAG_RATIO = 1/8, Z_INDEX_WHEN_STICK=500;

	var STICKY_FINISHED_EVENT = 'sticky_finished';

	var preventDrag = false;
	$(document).mousedown(function(e){if(preventDrag)e.preventDefault();})
	function doElement(interval) {
		var ratio = STICK_RATIO, tPos = p();
		var o=$(this), startPos=curPos(o), tmp=bind(o, onMove), bOnMove=function(e){tPos = tmp(tPos, e,ratio, startPos);};
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
		toString:function(){with(this)return "x: "+x+", y: "+y;},
		add:function(x,y){if(isO(x)){y=x.y;x=x.x}return p(this.x+x,this.y+y);},
		sub:function(x,y){if(isO(x)){y=x.y;x=x.x}return p(this.x-x,this.y-y);},
		mul:function(x,y){if(isO(x)){y=x.y;x=x.x}return p(this.x*x,this.y*y);}
	}
	function getPos(curPos, tPos, target) {
		var t = tPos.mul(ACCELERATION).add(target.sub(curPos).mul(CONVERT));
		return [curPos.add(t), t]
	}
	function setPos(o, pos) {
		return o.css('left', Math.round(pos.x)).css('top', Math.round(pos.y));
	}
	function onMove(tPos, e, ratio, start) {
		var o=this, mouse = p(e).sub(p(o.offset())).add(start);
		var delta = mouse.sub(start).add(o.width()*2, o.height()*2).mul(ratio,ratio);
		var cur = getPos(curPos(o), tPos, mouse.sub(delta));
		setPos(o, cur[0]);
		return cur[1];
	}
	function getSteps(target, cur, tPos, S) {
		if (Math.abs(target.x-cur.x) < .1 && Math.abs(target.y-cur.y) < .1)return[target, cur];
		var aCur = getPos(cur, tPos||p(), target);
		(S = getSteps(target, aCur[0], aCur[1])).push(cur)
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
