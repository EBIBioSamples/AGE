package uk.ac.ebi.age.model.impl.v1;

import java.io.Serializable;

import uk.ac.ebi.age.model.AgeSemanticElement;
import uk.ac.ebi.age.model.SemanticModel;

abstract class AgeSemanticElementImpl implements AgeSemanticElement, Serializable
{
 private static final long serialVersionUID = 1L;

 private SemanticModel model;
 
 public AgeSemanticElementImpl( SemanticModel m )
 {
  model=m;
 }
 
 @Override
 public SemanticModel getSemanticModel()
 {
  return model;
 }
 
// public void setSemanticModel( SemanticModel m )
// {
//  model=m;
// }

}
