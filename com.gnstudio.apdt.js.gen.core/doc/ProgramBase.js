var MyProgram = (function () { 
	
	// Container for public members. 
	var myProgram = {};
	
    /**
     *Place to add private members.    
     */
    var privateVariable = 1; 
     
    function privateMethod() { 
        // ... 
    }
    
    /**
     * Public members.
     */
    myProgram.publicProperty = 1; 
    myProgram.publicMethod = function() { 
        // ... 
    }; 

    // Use this method each time you need to define a namespace.
    myProgram.namespace = function(name) {

		if (name) {
	
			var names = name.split(".");
			var currentNamespace = myProgram;
	
			for (var nameIndex in names) {
				
				if (!currentNamespace[names[nameIndex]]) {
					currentNamespace[names[nameIndex]] = {};
				}
	
				currentNamespace = currentNamespace[names[nameIndex]];
	
			}
	
			return true;
	
		}
	
		return false;

    };

    return myProgram;

}());