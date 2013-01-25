package test;

public class Example9{
	   public static void main(String args[]){
	      int i,j,product=1;
	      double sum=0;
	      for(i=1;i<=20;i++){
	    	  product=1;
	         for(j=1;j<=i;j++)
	            product=product*j;
	         System.out.println(product);
	         sum=sum+1.0/product;
	         System.out.println(i+" 1+1/2!+...+1/20!=" +sum);
	      }
	      System.out.println("1+1/2!+...+1/20!=" +sum);
	   }
	}