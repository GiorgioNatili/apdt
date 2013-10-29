package com.gnstudio.syntax {
	
	import flash.events.IEventDispatcher;
	
	import mx.core.UIComponent;
	
	/**
	 * Example of documenting a class.
	 */
	internal final dynamic class ClassSyntax extends UIComponent implements IEventDispatcher {
		
		public static const myPsConst:int;
		public static var myPsVar:Number;
		
		protected static const myPrsConst:String;
		protected static var myPrsVar:Boolean;
		
		internal static const myIsConst:Array;
		internal static var myIsVar:int;
		
		private static const myPrisConst:Number;
		private static var myPrisVar:Number;
		
		internal const myIConst:Array;
		internal var myIVar:int;
		
		/**
		 * Documenting class members.
		 */
		protected const myPrConst:String;
		
		/**
		 * Example of the annotation on a variable. 
		 */
		[Bindable]
		protected var myPrVar:Boolean;
		
		private const myPriConst:Number;
		private var _myPriVar:Number;
		private var _myPriVar2:Number;
		
		/**
		 * ASDoc comment. Just like JavaDoc comment.
		 */
		public function ClassSyntax() {
			
			// Inline comment. Call super constructor.
			super();
			
			/*
				Block comment.
				Hello world!
			 */
			
		}
		
		/**
		 * Example of overridden method.
		 */
		override protected function updateDisplayList(unscaledWidth:Number, unscaledHeight:Number):void {
			
			super.updateDisplayList(unscaledWidth, unscaledHeight);
			
		} 
		
		/**
		 * Example of final method.
		 */
		final protected function myFinalMethod(arg1:int):String {
			
			return null;
			
		}
		
		/**
		 * Example of public method.
		 */
		public function myPublicMethod(arg1:int):Number {
			
			return null;
			
		}
		
		/**
		 * Sequence examples.
		 */
		public function sequenceExamples():void {
			
			// Loops.
			
			const limit:int = 300; // Constant.
			for (var i:int = 0; i <= limit; i++) {
				
				// Body.
				
			}
			
			var array1:Object = {}; // Variable.
			for (key:String in array1) {
				
				// Body.
				
			}
			
			var array2:Array = [];
			for each (o2:Object in array2) {
				
				// Body.
				
			}
			
			var j:int = 0;
			while (j < 5) {
				
				// Body.
				
			}
			
			var k:int = 5;
			do {
				
				// Body.
				
			} while (i < 5);
			
			
			// Conditions.			
			
			if (true) {
				
				
				
			} else if (false) {
				
				
				
			} else {
				
				
				
			}
			
			switch (true) {
				
				case "a":
					// Statement.
					break;
				
				case "b":
					// Statement.
					break;
				
				default:
					// Statement.
					break;
				
			}
			
			try {
				
				// Statement.
				
			} catch (error:Error) {
				
				// Statement.
				
			} finally {
				
				// Statement.
				
			}
			
			myPublicMethod(1); // Method call.
			
			throw new Error("My error!"); // Example of throw stetement.
			
			// RECURSION: Call my recursion method.
			
			// READ the age of the user FROM the server side method we have.
			
			// STORE the result into the age variable.
			
			// RECOVER the age limit by which user are not allowed to enter the web site.
			
			// PROMPT the error message.
			
			// CALCULATE and STORE the cost of the live event.
			
			// WRITE the information remotely to avoid to keep logic into the client TO server side container.
			
			// PRINT "welcome aboard dude!!!!".
			
		}
		
		//----------------------------------
		//  myPriVar
		//----------------------------------
		
		/**
		 * Example of accessor methods.
		 * ASDoc comment goes only on the getter.
		 * 
		 * Example of the annotation on a accessor methods.
		 * Note that annotaion goes only before the getter.
		 */
		
		[Bindable]
		
		public function get myPriVar():Number {
			
			return _myPriVar;
			
		}

		public function set myPriVar(value:Number):void {
			
			_myPriVar = value;
			
		}

		//----------------------------------
		//  myPriVar2 
		//----------------------------------
		
		/**
		 * Example of accessor methods.
		 * ASDoc comment goes only on the getter.
		 */
		public function get myPriVar2():Number {
			
			return _myPriVar2;
			
		}
		
		public function set myPriVar2(value:Number):void {
			
			_myPriVar2 = value;
			
		}
		
	}
	
}