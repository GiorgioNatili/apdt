<?php

namespace com\gnstudio\syntax;
	
/**
 * Example of documenting a class.
 */
abstract class ClassSyntax extends AnotherClass implements ISomeInterface {
	
	/**
	 * constant
	 */
	const myConstant = 'some constant';
	
	/**
	 * public field
	 */
	public $myPublicField = 0;

	/**
	 * protected field
	 */
	protected $myProtectedField = ' some value';

	/**
	 * private field
	 */
	private $myPrivateField = array('foo','bar');
	

	/**
	 * PHPDoc comment. Just like JavaDoc comment.
	 * construsctor
	 */
	public function __construct(){
		
		// Inline comment. Call super constructor.
		parent::__construct();
		
		/*
			Block comment.
			Hello world!
		 */
		
	}

	/**
	 * activate if you need explicit class destruction
	 * destruction phase is usually managed by web server
	 */
	//function __destruct(){
		
		//parent::__destruct();
		
	//}
	
	/**
	 *  Example of public method
	 */
	public function somePublicFunction() {

	}

	/**
	 *  Example of protected method
	 */
	public function someProtectedFunction() {

	}

	/**
	 *  Example of private method
	 */
	public function somePrivateFunction() {

	}
	
	/**
	 * Exmaple overload method
	 */
	public function someOverloadedFunction(){
		
		parent::someOverLoadedFunction();
	
	}
	
	/**
	 * Sequence examples.
	 */
	public function sequenceExamples() {
		
		// Loops.
		
		$limit = 300;
		
		for ($i = 0; $i <= $limit; $i++) {
			
			// Body.
			
		}
		
		$array1 = array('foo' => 'bar'); // Variable.
		
		foreach($array1 as $key => $value) {
			
			// Body.
			
		}
		
		$array2 = array('foo', 'bar');
		
		foreach($array2 as $value) {
			
			// Body.
			
		}
		
		$j = 0;
		
		while ($j < 5) {
			
			// Body.
			
		}
		
		$k = 5;
		
		do {
			
			// Body.
			
		} while ($i < 5);
		
		
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
			
		} catch (Exception $e) {
			
			// Statement.
			
		}
		
		myPublicMethod(1); // Method call.
		
		throw new Exception("My error!"); // Example of throw stetement.
		
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
	 * PHPDoc comment goes only on the getter.
	 * 
	 * Example of the annotation on a accessor methods.
	 * Note that annotaion goes only before the getter.
	 */
	

	public function __get($prop) {
		
		if (property_exists($this, $prop)) {
			return $this->$prop;
		}
	}


	public function __set($prop, $value) {
	
		if (property_exists($this, $prop)) {
			$this->$prop = $value;
		}

		return $this;
	}

}

?>
