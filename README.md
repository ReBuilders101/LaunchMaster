# LaunchMaster
Allows you to launch multiple sub-programs with arguments from a GUI

## How to use
Subprograms are marked with annotations. A subprogram must fulfill the following conditions:
1. The class must be marked with `@Program(name="<Unique Name>",desc="<Program Description>")`
2. They must contain one **static** method marked with `@MainMethod`
3. Every parameter of this method must have a `@Param(desc="<Parameter Description>")` annotation
   They also must be one of these types: `int`,`double`,`boolean`,`String`.
  #### Optional arguments for `@Param:`
  * `def=<doble>` Default value for numeric types and boolean. For ints, def is rounded, for booleans, 0 means false and everything else means true (You should only use 1 for true).
  * `defStr=<String>` Default value for Strings. Has no effect on any other type than String.
  * `min=<double>` Minimum value for numeric types and String. For ints, def is rounded, for Strings, this is the minumum amount of characters in this String.
  * `max=<double>` Maximum value for numeric types and String. For ints, def is rounded, for Strings, this is the maximum amount of characters in this String.
4. Create a new LaunchMaster instance with `LaunchMaster.create(title, rootPackageName);` LaunchMaster will then scan all classes in this Package and its subpackages and add all classes that fulfill the conditions.
5. If you have a class that is not in one of the specified packages, vou can add it manually by calling `lauchMasterInstance.addProgram(MyProgramClass.class);`