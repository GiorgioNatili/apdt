package com.gnstudio.syntax {
	
	import flash.events.IEventDispatcher;
	
	/**
	 * Example of documenting interface.
	 */	
	public interface IInterfaceSyntax extends IEventDispatcher {
		
		/**
		 * Example of iterface method.
		 */
		function myMethod(arg1:int, arg2:Array, arg3:Number):String;
		
		/**
		 * Example of iterface property.
		 */
		function get myProperty():String;
		function set myProperty(value:String):void;
		
	}
	
}