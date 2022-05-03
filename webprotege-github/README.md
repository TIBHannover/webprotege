# GitHub module description

## How to run git commands

1. Add dependency to pom.xml to a module where you will call git commands
   ```
   <dependency>
      <groupId>${project.groupId}</groupId>
      <artifactId>webprotege-github</artifactId>
      <version>${project.version}</version>
   </dependency>
   ```

2. Create instance of class GitCommandsServiceImpl
    ```
   GitCommandsService gitCommandsService = new GitCommandsServiceImpl();
   ```

3. Call methods with git commands
   
   Example:

    ```
   gitCommandsService.gitCloneGitHub(<token>, <repo_owner>, <repo_name>, uploadsDirectory.getPath());
   ```