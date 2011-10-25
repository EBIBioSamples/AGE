package uk.ac.ebi.age.model.impl.v1;

import java.io.Serializable;

import uk.ac.ebi.age.model.AgeContextSemanticElement;
import uk.ac.ebi.age.model.ContextSemanticModel;

abstract class AgeContextSemanticElementImpl extends AgeSemanticElementImpl implements AgeContextSemanticElement, Serializable
{
 private static final long serialVersionUID = 1L;

 
 public AgeContextSemanticElementImpl( ContextSemanticModel m )
 {
  super( m );
 }
 
 @Override
 public ContextSemanticModel getSemanticModel()
 {
  return (ContextSemanticModel)super.getSemanticModel();
 }
 
// @Override
// public void setSemanticModel( ContextSemanticModel m )
// {
//  super.setSemanticModel(m);
// }

// @Override
// public void setSemanticModel( SemanticModel m )
// {
//  throw new UnsupportedOperationException();
// }

}
