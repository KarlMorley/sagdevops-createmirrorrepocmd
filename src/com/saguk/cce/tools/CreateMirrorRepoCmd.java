package com.saguk.cce.tools;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.yaml.snakeyaml.Yaml;

/**
 * The following utility extracts all of the products from a Command Central template and constructs
 * a repository mirror add command with the required products.
 * 
 * @version 0.1
 */
public class CreateMirrorRepoCmd {
  
  @SuppressWarnings("unchecked")
  public static void main(String[] args) {
    try {
      // Discover the classname
      String className = CreateMirrorRepoCmd.class.getName();
      className = className.substring(className.lastIndexOf('.') + 1);
      
      // Parse the command Line
      CmdLineParser cmdLine = new CmdLineParser();
      CmdLineArgs cmdArgs = cmdLine.parse(className,args);
      
      // Create a list to hold the master list of products
      List<String> productList = new ArrayList<String>(); 
      InputStream yamlFile = new FileInputStream(new File(cmdArgs.getYamlFile()));  

      // Parse the YAML document into a String Array 
      Yaml yaml = new Yaml();

      Map<String, ArrayList<String>> ccTemplateYaml = 
          (Map<String, ArrayList<String>>) yaml.load(yamlFile);

      CompositeTemplateParser ccTemplate = new CompositeTemplateParser(ccTemplateYaml);
      productList = ccTemplate.getProducts();
      
      // Create the Mirror Product artifacts output line
      StringBuffer mirrorProdCmd = new StringBuffer();
      mirrorProdCmd.append("sagcc add repository products mirror name=");
      mirrorProdCmd.append(cmdArgs.getMirrorRepo());
      mirrorProdCmd.append(" sourceRepos=");
      mirrorProdCmd.append(cmdArgs.getSourceRepo());
      mirrorProdCmd.append(" artifacts=");
      int count = 0;
      for (String product : productList) {
    	  mirrorProdCmd.append(product);
        count++;
        if (count < productList.size()) {
        	mirrorProdCmd.append(",");
        }
      }
      mirrorProdCmd.append(" platforms=");
      mirrorProdCmd.append(cmdArgs.getPlatforms());
      System.out.println(mirrorProdCmd);
      
      // Create the Mirror Fix artifacts output line
      StringBuffer mirrorFixCmd = new StringBuffer();
      mirrorFixCmd.append("sagcc add repository fixes mirror name=");
      mirrorFixCmd.append(cmdArgs.getMirrorRepo());
      mirrorFixCmd.append("-FIX sourceRepos=Empower");
      mirrorFixCmd.append(" productRepos=");
      mirrorFixCmd.append(cmdArgs.getMirrorRepo());
      mirrorFixCmd.append(" artifacts=LATEST");
      System.out.println(mirrorFixCmd);
      
    } catch (FileNotFoundException e) {
      System.err.println(e.getMessage());
    } catch (IllegalArgumentException e) {
      System.err.println("Error parsing command line parameters");
    }
  }
 
}
