﻿class Photon {
	private var isVisible:Boolean;
	private var xLoc:Number;
	private var yLoc:Number;
	private var ds:Number = 10;
	private var s:Number = 15;
	private var rgb:Number;
	private var wavelength:Number;	
	private var theta:Number;
	private static var instances:Array = new Array();
	private static var init:Boolean = false;
	static function stepInstances() {
		for (var i = 0; i < Photon.instances.length; i++) {
			instances[i].stepInTime();
		}
	}
	static function deleteInstance(p) {
		var pTest:Photon;
		for (var i = 0; i < Photon.instances.length; i++) {
			// NOTE!!! If Photon.instances[i] doesn't work!!!
			var p2 = instances[i];
//			if (Photon.instances[i] == p) {
			if (instances[i] == p) {
				var l = instances.length;
				Photon.instances.splice(i, 1);
				var l2 = instances.length;
			}
		}
	}
	static function getInstances() {
		return instances;
	}
	static function getPhotonsWithinBounds(x, y, w, h) {
		var result = new Array();
		for (var i = 0; i < Photon.instances.length; i++) {
			var p = Photon.instances[i];
			if (p.x >= x && p.x <= x + w && p.y >= y && p.y <= y + h) {
				result.push(p);
			}
		}
		return result;
	}
	function Photon(x, y, theta, wavelength) {
		this.xLoc = x;
		this.yLoc = y;
		this.theta = theta;
		this.wavelength = wavelength;
		this.rgb = ColorUtil.getColor(wavelength);
		this.isVisible = true;
		Photon.instances.push(this);
	}
	function getX():Number {
		return xLoc;
	}
	function getWavelength():Number{
		return this.wavelength;
	}
	function setRgb(rgb) {
		this.rgb = rgb;
	}
	function getRgb():Number {
		return rgb;
	}
	function setIsVisible(isVisible:Boolean):Void {
		this.isVisible = isVisible;
	}
	function stepInTime() {
		this.xLoc += ds * Math.cos(this.theta);
		this.yLoc += ds * Math.sin(this.theta);
	}
	function paint(g) {
		this.stepInTime();
		if (this.isVisible) {
			g.lineStyle(1, this.rgb, 100);
			g.moveTo(this.xLoc, this.yLoc);
			g.lineTo(this.xLoc - s * Math.cos(this.theta), this.yLoc - s * Math.sin(this.theta));
		}
	}
}
