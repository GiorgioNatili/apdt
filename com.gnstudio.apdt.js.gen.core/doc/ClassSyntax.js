var MyProgram = (function (_myProgram) {
    
    // Define a namespace for a class.
    _myProgram.namespace("com.gnstudio.utils");
    
	/**
	 * JavaScript comment should be just like JavaDoc comment.
	 */
    _myProgram.com.gnstudio.utils.MyClass = function(radius) {
	
		// Class member.
		this.radius = radius;
		
		// Class method.
		this.myMethod = function() {
	
			// Method body.
			
			// Loops.
				
			var limit = 300;
			var i = 0;
			for (i = 0; i <= limit; i++) {
				
				// Body.
				
			}
				
			var o = {};
			for (key in o) {
				
				// Body.
				
			}
			
			var j = 0;
			while (j < 5) {
				
				// Body.
				
			}
			
			var k = 0;
			do {
				
				// Body.
				
			} while (k < 5);
			
			
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
				
			} catch (error) {
				
				// Statement.
				
			}
			
			myPublicMethod(1); // Method call.
			
			throw "My error!"; // Example of throw statement.
			
			// RECURSION: Call my recursion method.
			
			// READ the age of the user FROM the server side method we have.
			
			// STORE the result into the age variable.
			
			// RECOVER the age limit by which user are not allowed to enter the web site.
			
			// PROMPT the error message.
			
			// CALCULATE and STORE the cost of the live event.
			
			// WRITE the information remotely to avoid to keep logic into the client TO server side container.
			
			// PRINT "welcome aboard dude!!!!".		
	
		};
	
		/**
		 * Example of accessor methods.
		 * Comment goes only on the getter.
		 */	
		this.getArea = function() {
	
			return (this.radius * this.radius) * Math.PI;
	
		};
	
		this.setArea = function(area) {
	
			this.area = area;
	
		};

    };
    
     // Inheritance example: MyClass extends MySuperClass.
     _myProgram.com.gnstudio.utils.MyClass.prototype = new MySuperClass();

    return _myProgram;

}(MyProgram));