package uk.ac.ebi.age.parser;

import java.util.List;

public interface BlockHeader
{

 public abstract void setClassColumnHeader(ClassReference cc);

 public abstract void addColumnHeader(ClassReference chd);

 public abstract ClassReference getClassColumnHeader();

 public abstract List<ClassReference> getColumnHeaders();
 
 AgeTabModule getModule();
 
 public abstract boolean isHorizontal();

 public abstract void setHorizontal(boolean horizontal);

}
