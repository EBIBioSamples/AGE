package uk.ac.ebi.age.model;

import java.util.Collection;

public interface Attributed extends AgeContextSemanticElement
{
 AttributedClass getAttributedClass();

 Collection<? extends AgeAttribute> getAttributes();

 AgeAttribute getAttribute(AgeAttributeClass cls);

// Collection< ? extends AgeAttribute> getAttributes(AgeAttributeClass cls);
 Collection< ? extends AgeAttribute> getAttributesByClass(AgeAttributeClass cls, boolean wSubCls);

 // Collection<String> getAttributeClassesIds();

// Collection< ? extends AgeAttribute> getAttributesByClassId(String cid, boolean wSubCls);

 Collection< ? extends AgeAttributeClass> getAttributeClasses();

}