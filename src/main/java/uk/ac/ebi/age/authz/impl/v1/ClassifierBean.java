package uk.ac.ebi.age.authz.impl.v1;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

import uk.ac.ebi.age.authz.writable.ClassifierWritable;
import uk.ac.ebi.age.authz.writable.TagWritable;
import uk.ac.ebi.mg.collection.Named;

public class ClassifierBean implements Named<String>, Serializable, ClassifierWritable
{
 private static final long serialVersionUID = 1L;

 private String id;
 private String description;
 private Collection<TagWritable> tags = new ArrayList<TagWritable>();
 
 ClassifierBean()
 {}
 
 @Override
 public String getId()
 {
  return id;
 }
 
 @Override
 public void setId(String id)
 {
  this.id = id;
 }
 
 @Override
 public String getDescription()
 {
  return description;
 }
 
 @Override
 public void setDescription(String description)
 {
  this.description = description;
 }

 @Override
 public Collection<TagWritable> getTags()
 {
  return tags;
 }

 @Override
 public TagWritable getTag(String tagId)
 {
  for(TagWritable t : tags)
  {
   if( t.getId().equals(tagId))
    return t;
  }
  
  return null;
 }

 @Override
 public void addTag(TagWritable tb)
 {
  tags.add(tb);
 }


}
