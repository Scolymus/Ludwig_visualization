# Ludwig visualization

This software plays Ludwig output files in 3D. It can play particles and order parameter field (up to 2 parameters).

**HOW TO INSTALL IT IN ECLIPSE**

1) File->Import->Git->Projects from Git (with smart import)
2) Clone URI
3) Write "https://github.com/Scolymus/Ludwig_visualization" in URI textfield (without quotes)
4) Next
5) Next
6) Next
7) Finish

**HOW TO RUN IT FROM ECLIPSE**

1) Run->Run as->Maven build...
2) Write "clean compile exec:java -Dexec.mainClass=main.main" (without quotes) in Goals textfield
3) Apply
4) Run

**HOW TO USE IT**

Open src/main/java (it's on your left under the project name).
Open main package (it is under src/main/java as a box in brown divided in 4 parts). 
Open main.java.

Inside "public static void main(String[] args) {" edit the firsts lines until "//########### DON'T MODIFY AFTER THIS LINE #########################"
Run 
