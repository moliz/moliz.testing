package org.modelexecution.fumltesting.execution;

import java.io.File;
import java.util.List;

import org.modelexecution.fuml.Syntax.Classes.Kernel.Package;
import org.modelexecution.fumltesting.ocl.internal.model.FUMLClass;
import org.modelexecution.fumltesting.ocl.internal.provider.FUMLModelInstanceProvider;
import org.modelexecution.fumltesting.ocl.internal.provider.FUMLModelProvider;

import tudresden.ocl20.pivot.essentialocl.standardlibrary.OclAny;
import tudresden.ocl20.pivot.essentialocl.standardlibrary.OclBoolean;
import tudresden.ocl20.pivot.facade.Ocl2ForEclipseFacade;
import tudresden.ocl20.pivot.interpreter.IInterpretationResult;
import tudresden.ocl20.pivot.interpreter.IOclInterpreter;
import tudresden.ocl20.pivot.interpreter.OclInterpreterPlugin;
import tudresden.ocl20.pivot.model.metamodel.IMetamodel;
import tudresden.ocl20.pivot.modelinstance.IModelInstance;
import tudresden.ocl20.pivot.modelinstancetype.types.IModelInstanceObject;
import tudresden.ocl20.pivot.parser.ParseException;
import tudresden.ocl20.pivot.pivotmodel.ConstrainableElement;
import tudresden.ocl20.pivot.pivotmodel.Constraint;

public class FumlOclInterpreter {
	private FumlOclInterpreter(){
		metamodel = Ocl2ForEclipseFacade.getMetaModel("tudresden.ocl20.pivot.metamodels.fuml");
		modelProvider = (FUMLModelProvider) metamodel.getModelProvider();
		modelInstanceProvider = new FUMLModelInstanceProvider();
	}
	private static FumlOclInterpreter INSTANCE;
	
	private List<Constraint> constraints;
	
	private FUMLModelProvider modelProvider;
	private FUMLModelInstanceProvider modelInstanceProvider;
	
	private IMetamodel metamodel;
	
	private IOclInterpreter oclInterpreter;
	
	public static FumlOclInterpreter getInstance(){
		if(INSTANCE == null)
			INSTANCE = new FumlOclInterpreter();
		return INSTANCE;
	}
	
	public IMetamodel getMetamodel(){
		return metamodel;
	}
	
	public void setModel(Package modelPackage){
		getInstance().modelProvider.initModel(modelPackage);		
	}
	
	public void loadConstraints(File oclFile)throws ParseException{
		constraints = Ocl2ForEclipseFacade.parseConstraints(oclFile, modelProvider.getModel(), false);
	}
	
	public IModelInstance getEmptyModelInstance(){
		return modelInstanceProvider.createEmptyModelInstance(modelProvider.getModel());
	}
	
	public boolean evaluateConstraint(String constraintName, IModelInstance modelInstance){
		Constraint constraint = null;
		for(Constraint aConstraint: constraints){
			if(aConstraint.getName().equals(constraintName))constraint = aConstraint;
		}
		if(constraint != null){
			//TODO continue
			System.out.println("Checking constraint.. " + constraint.getSpecification().getBody());
			
			oclInterpreter = OclInterpreterPlugin.createInterpreter(modelInstance);
			ConstrainableElement constrainedElement = constraint.getConstrainedElement().get(0);
			if(constrainedElement instanceof FUMLClass){
				FUMLClass constrainedClass = (FUMLClass)constrainedElement;
				for(IModelInstanceObject object: modelInstance.getAllInstances(constrainedClass)){
					IInterpretationResult result = oclInterpreter.interpretConstraint(constraint, object);
					OclAny resultValue = result.getResult();
					if(resultValue instanceof OclBoolean){
						OclBoolean resultBoolean = (OclBoolean)resultValue;
						return resultBoolean.isTrue();
					}
				}
			}			
		}					
		return true;
	}
}