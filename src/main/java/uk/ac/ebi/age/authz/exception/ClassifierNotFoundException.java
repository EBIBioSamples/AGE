package uk.ac.ebi.age.authz.exception;

public class ClassifierNotFoundException extends TagException
{
 public ClassifierNotFoundException()
 {
  super("Classifier not found");
 }
}
