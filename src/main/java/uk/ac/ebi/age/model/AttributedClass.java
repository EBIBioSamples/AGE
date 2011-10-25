package uk.ac.ebi.age.model;

import java.util.Collection;

public interface AttributedClass
{
 Collection<AttributeAttachmentRule> getAttributeAttachmentRules();
 Collection<AttributeAttachmentRule> getAllAttributeAttachmentRules();
 
 String getName();
 boolean isCustom();
}
