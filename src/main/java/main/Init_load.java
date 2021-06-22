package main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.DoubleBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;

import main.main.dirc;

/*######################################################
#          			LUDWIG VISUALIZER     		       #  
#                                                      #  
#        Author: Scolymus                              #
#        License: CC BY-NC-SA 4.0                      #
#                                                      #
#------------------------------------------------------#*/

public class Init_load {
	/**
	 *  Starting camera positions. If second value is added, then camera will
	 *  look towards this point.
	 *  @param from: dirc.value.ordinal()
	 *  @param to: dirc.value.ordinal() or -1 if you want to point towards the
	 *  			opposite wall to the one you starts.
	 */
	public static void camera_start(int from, int to){
		
		float value = 1.1f;
		
		//First we select from where the camera is located
		//If to = -1 select also the front wall
		//The idea is to center the camera in the field. E.g.:
		//If we want a X plane, we center the camera at the middle of YZ coordinates
		//About right and up, look at camera.png in resources folder or read OPENGL documentation
		
		if (from == dirc.X_BASE.ordinal()){
			main.position[0] = (1.f-value)*main.NX/main.radius_particle;
			main.position[1] = (main.NY/2.f)/main.radius_particle;
			main.position[2] = (main.NZ/2.f)/main.radius_particle;
			
			if (to == -1) {
				main.direction[0] = (value*main.NX)/main.radius_particle;
				main.direction[1] = (main.NY/2.f)/main.radius_particle;
				main.direction[2] = (main.NZ/2.f)/main.radius_particle;
				
				main.right[0] = 0.f;
				main.right[1] = -1.f;
				main.right[2] = 0.f;
				
				main.up[0] = 0.f;
				main.up[1] = 0.f;
				main.up[2] = 1.f;
				
			}
		} else if (from == dirc.X_TOP.ordinal()){
			main.position[0] = (value*main.NX)/main.radius_particle;
			main.position[1] = (main.NY/2.f)/main.radius_particle;
			main.position[2] = (main.NZ/2.f)/main.radius_particle;
			
			if (to == -1) {
				main.direction[0] = (1.f-value)*main.NX/main.radius_particle;
				main.direction[1] = (main.NY/2.f)/main.radius_particle;
				main.direction[2] = (main.NZ/2.f)/main.radius_particle;
				
				main.right[0] = 0.f;
				main.right[1] = 1.f;
				main.right[2] = 0.f;
				
				main.up[0] = 0.f;
				main.up[1] = 0.f;
				main.up[2] = 1.f;
			}
		} else if (from == dirc.Y_BASE.ordinal()){
			main.position[0] = (main.NX/2.f)/main.radius_particle;
			main.position[1] = (1.f-value)*main.NY/main.radius_particle;
			main.position[2] = (main.NZ/2.f)/main.radius_particle;
			
			if (to == -1) {
				main.direction[0] = (main.NX/2.f)/main.radius_particle;
				main.direction[1] = (value*main.NY)/main.radius_particle;
				main.direction[2] = (main.NZ/2.f)/main.radius_particle;
								
				main.right[0] = 1.f;
				main.right[1] = 0.f;
				main.right[2] = 0.f;
				
				main.up[0] = 0.f;
				main.up[1] = 0.f;
				main.up[2] = 1.f;
			}
		} else if (from == dirc.Y_TOP.ordinal()){
			main.position[0] = (main.NX/2.f)/main.radius_particle;
			main.position[1] = (value*main.NY)/main.radius_particle;
			main.position[2] = (main.NZ/2.f)/main.radius_particle;
			
			if (to == -1) {
				main.direction[0] = (main.NX/2.f)/main.radius_particle;
				main.direction[1] = (1.f-value)*main.NY/main.radius_particle;
				main.direction[2] = (main.NZ/2.f)/main.radius_particle;
			}			
			
			main.right[0] = -1.f;
			main.right[1] = 0.f;
			main.right[2] = 0.f;
			
			main.up[0] = 0.f;
			main.up[1] = 0.f;
			main.up[2] = 1.f;
		} else if (from == dirc.Z_BASE.ordinal()){
			main.position[0] = (main.NX/2.f)/main.radius_particle;
			main.position[1] = (main.NY/2.f)/main.radius_particle;
			main.position[2] = (1.f-value)*main.NZ/main.radius_particle;
			
			if (to == -1) {
				main.direction[0] = (main.NX/2.f)/main.radius_particle;
				main.direction[1] = (main.NY/2.f)/main.radius_particle;
				main.direction[2] = value*main.NZ/main.radius_particle;	
			}
			
			main.right[0] = -1.f;
			main.right[1] = 0.f;
			main.right[2] = 0.f;
			
			main.up[0] = 0.f;
			main.up[1] = 1.f;
			main.up[2] = 0.f;
		} else if (from == dirc.Z_TOP.ordinal()){
			main.position[0] = (main.NX/2.f)/main.radius_particle;
			main.position[1] = (main.NY/2.f)/main.radius_particle;
			main.position[2] = value*main.NZ/main.radius_particle;
			
			if (to == -1) {
				main.direction[0] = (main.NX/2.f)/main.radius_particle;
				main.direction[1] = (main.NY/2.f)/main.radius_particle;
				main.direction[2] = (1.f-value)*main.NZ/main.radius_particle;	
			}
			
			main.right[0] = 1.f;
			main.right[1] = 0.f;
			main.right[2] = 0.f;
			
			main.up[0] = 0.f;
			main.up[1] = 1.f;
			main.up[2] = 0.f;
		}
		
		//Now we select where the camera points
		if (to != -1){
			if (from == dirc.X_BASE.ordinal()){
				main.direction[0] = (1.f-value)*main.NX/main.radius_particle;
				main.direction[1] = (main.NY/2.f)/main.radius_particle;
				main.direction[2] = (main.NZ/2.f)/main.radius_particle;
			} else if (from == dirc.X_TOP.ordinal()){
				main.direction[0] = (value*main.NX)/main.radius_particle;
				main.direction[1] = (main.NY/2.f)/main.radius_particle;
				main.direction[2] = (main.NZ/2.f)/main.radius_particle;
			} else if (from == dirc.Y_BASE.ordinal()){
				main.direction[0] = (main.NX/2.f)/main.radius_particle;
				main.direction[1] = (1.f-value)*main.NY/main.radius_particle;
				main.direction[2] = (main.NZ/2.f)/main.radius_particle;
			} else if (from == dirc.Y_TOP.ordinal()){
				main.direction[0] = (main.NX/2.f)/main.radius_particle;
				main.direction[1] = (value*main.NY)/main.radius_particle;
				main.direction[2] = (main.NZ/2.f)/main.radius_particle;
			} else if (from == dirc.Z_BASE.ordinal()){
				main.direction[0] = (main.NX/2.f)/main.radius_particle;
				main.direction[1] = (main.NY/2.f)/main.radius_particle;
				main.direction[2] = (1.f-value)*main.NZ/main.radius_particle;
			} else if (from == dirc.Z_TOP.ordinal()){
				main.direction[0] = (main.NX/2.f)/main.radius_particle;
				main.direction[1] = (main.NY/2.f)/main.radius_particle;
				main.direction[2] = value*main.NZ/main.radius_particle;
			}
		}				
	}

	/**
	 *  Generate the Janus vertexes to draw the sphere.
	 *  A Janus is a sphere, hence we can localize vertexes with a latitude-longitude coordinates.
	 *  We can choose the number of vertexes by setting the number of vertexes per latitude and per longitude
	 *   
	 *  @param latitudes (int). Number of vertexes per latitude line
	 *  @param longitudes (int). Number of vertexes per longitude line
	 *  @param radius (float). Radius of particle. Everything is normalized to the radius of the particle, so write 0.5f.
	 *  @param portion (float). Vertexes will be painted in one of two colours, to describe the Janus. If you divide
	 *  	the particle in two sections, portion defines how much will one colour and the other. E.g. 0.5f is half, half
	 *  	as a true Janus particle. You can self-modify this part to apply different colours and change the parts of each section
	 *   
	 */
	public static void generate_vertexes_janus(int latitudes, int longitudes, float radius, float portion) {

        float latitude_increment = 360.0f / latitudes;
		float longitude_increment = 180.0f / longitudes;
				
		main.janusv = new float[2*4*(latitudes)*(longitudes)]; 
		main.janusc = new float[2*4*(latitudes)*(longitudes)];  
		
	    int spherePointCounter = 0;
	    int texturePointCounter = 0;
        float inactive[] = {1.0f, 1.f, 1.f};
        float active[] = {0.f, 0.f, 0.f};
        
		for (float u = 0; u < 360.0f; u += latitude_increment) {
			for (float t = 0; t < 180.0f; t += longitude_increment) {
				float rad = radius;
				float x = (float) (rad * Math.sin(Math.toRadians(t)) * Math.sin(Math.toRadians(u)));
				float y = (float) (rad * Math.cos(Math.toRadians(t)));
				float z = (float) (rad * Math.sin(Math.toRadians(t)) * Math.cos(Math.toRadians(u)));

				main.janusv[spherePointCounter++] = x;
				main.janusv[spherePointCounter++] = y;
				main.janusv[spherePointCounter++] = z;
				main.janusv[spherePointCounter++] = 1.f;
				
				//Notice that the colour will be applied later, not in this function.
				//Here we only select which are the both parts of the particle
				if (z<radius*(2*portion-1)){
					main.janusc[texturePointCounter++] = active[0];
					main.janusc[texturePointCounter++] = active[1];
					main.janusc[texturePointCounter++] = active[2];              
                }else{
                	main.janusc[texturePointCounter++] = inactive[0];
                	main.janusc[texturePointCounter++] = inactive[1];
                	main.janusc[texturePointCounter++] = inactive[2];
                }
				
				main.janusc[texturePointCounter++] = 1.f;

				float x1 = (float) (rad * Math.sin(Math.toRadians(t + longitude_increment)) * Math.sin(Math.toRadians(u + latitude_increment)));
				float y1 = (float) (rad * Math.cos(Math.toRadians(t + longitude_increment)));
		        float z1 = (float) (rad * Math.sin(Math.toRadians(t + longitude_increment)) * Math.cos(Math.toRadians(u + latitude_increment)));

		        main.janusv[spherePointCounter++] = x1;
		        main.janusv[spherePointCounter++] = y1;
		        main.janusv[spherePointCounter++] = z1;
		        main.janusv[spherePointCounter++] = 1f;
						        
                if (z1<radius*(2*portion-1)){
                	main.janusc[texturePointCounter++] = active[0];
                	main.janusc[texturePointCounter++] = active[1];
                	main.janusc[texturePointCounter++] = active[2];              
                }else{
                	main.janusc[texturePointCounter++] = inactive[0];
                	main.janusc[texturePointCounter++] = inactive[1];
                	main.janusc[texturePointCounter++] = inactive[2];
                }

                main.janusc[texturePointCounter++] = 1.f;
		    }
		}
	}
	
	/**
	 *  Generate the box vertexes to draw the lattice cells units.
	 *   
	 *  @param x (int). X size for the lattice (NX in Ludwig's input)
	 *  @param y (int). Y size for the lattice (NY in Ludwig's input)
	 *  @param z (int). Z size for the lattice (NZ in Ludwig's input)
	 *   
	 */
	public static void generate_vertexes_phi(int x, int y, int z) {

		main.boxAxis = new float[4*x*y*z]; 
		
	    int spherePointCounter = 0;
        
		for (int u = 0; u < x; u++) {
			for (int v = 0; v < y; v++) {
				for (int w = 0; w < z; w++) {			

					main.boxAxis[spherePointCounter++] = u/main.radius_particle;
					main.boxAxis[spherePointCounter++] = v/main.radius_particle;
					main.boxAxis[spherePointCounter++] = w/main.radius_particle;
					main.boxAxis[spherePointCounter++] = 1.f;

				}
		    }
		}
	}
	
	/**
	 *  Generate the wall vertexes to draw the walls in case you need them. 
	 */
	public static void generate_vertexes_walls(){
		main.wallsv = new float[3][16];// 3 walls  |  4 vertexes/wall * 4 components
		main.wallsc = new float[16];		
		
		int i;
		for (i = 0; i<4; i++){
			main.wallsc[i++] = 0.58f;//0.f;
			main.wallsc[i++] = 0.58f;//0.f;
			main.wallsc[i++] = 0.58f;//0.f;
			main.wallsc[i++] = 1.f;
		}
		
		//	X:
		main.wallsv[0][0] = 0.f;
		main.wallsv[0][1] = 0.f;
		main.wallsv[0][2] = 0.f;
		main.wallsv[0][3] = 1.f;
		
		main.wallsv[0][4] = 0.f;
		main.wallsv[0][5] = main.NY/main.radius_particle;
		main.wallsv[0][6] = 0.f;
		main.wallsv[0][7] = 1.f;
		
		main.wallsv[0][8] = 0.f;
		main.wallsv[0][9] = main.NY/main.radius_particle;
		main.wallsv[0][10] = main.NZ/main.radius_particle;
		main.wallsv[0][11] = 1.f;
		
		main.wallsv[0][12] = 0.f;
		main.wallsv[0][13] = 0.f;
		main.wallsv[0][14] = main.NZ/main.radius_particle;
		main.wallsv[0][15] = 1.f;		
		
		//	Y:
		main.wallsv[1][0] = 0.f;
		main.wallsv[1][1] = 0.f;
		main.wallsv[1][2] = 0.f;
		main.wallsv[1][3] = 1.f;
		
		main.wallsv[1][4] = main.NX/main.radius_particle;
		main.wallsv[1][5] = 0.f;
		main.wallsv[1][6] = 0.f;
		main.wallsv[1][7] = 1.f;
		
		main.wallsv[1][8] = main.NX/main.radius_particle;
		main.wallsv[1][9] = 0.f;
		main.wallsv[1][10] = main.NZ/main.radius_particle;
		main.wallsv[1][11] = 1.f;
		
		main.wallsv[1][12] = 0.f;
		main.wallsv[1][13] = 0.f;
		main.wallsv[1][14] = main.NZ/main.radius_particle;
		main.wallsv[1][15] = 1.f;
		
		//	Z:
		main.wallsv[2][0] = 0.f;
		main.wallsv[2][1] = 0.f;
		main.wallsv[2][2] = 0.f;
		main.wallsv[2][3] = 1.f;
		
		main.wallsv[2][4] = 0.f;
		main.wallsv[2][5] = main.NY/main.radius_particle;
		main.wallsv[2][6] = 0.f;
		main.wallsv[2][7] = 1.f;
		
		main.wallsv[2][8] = main.NX/main.radius_particle;
		main.wallsv[2][9] = main.NY/main.radius_particle;
		main.wallsv[2][10] = 0.f;
		main.wallsv[2][11] = 1.f;
		
		main.wallsv[2][12] = main.NX/main.radius_particle;
		main.wallsv[2][13] = 0.f;
		main.wallsv[2][14] = 0.f;
		main.wallsv[2][15] = 1.f;
		
	}
	
	/**
	 *  Load Janus positions.
	 *  
	 *  We expect to previously have a text file, where you should have the following columns:
	 *  Particle id (from 0 to the last, increasing in 1 unit each)	TIME STEP	X	Y	Z	MX	MY	MZ
	 *  If you don't want to have it like this, consider look at load_phi to write a similar function
	 *  for colloids loading.
	 *   
	 *  @param route (String). Route for Janus position particles.
	 *   
	 */	
	public static void load_janus_old(String route){		
		//This array will contain all the data for particles
		main.Janus_pos = new ArrayList<ArrayList<ArrayList<Float>>>(0);
		
		//I structure it as 5 subarrays:
		//0: x |  1: y  |  2: z  |  3: theta  |  4: phi	
		//Then, at each subarray we will have different arrays. Each array is a different particle.
		//Hence you access for example as [0][0] which means [x][particle 1]
		for (int i = 0; i<5;i++){
			main.Janus_pos.add(new ArrayList<ArrayList<Float>>(0));	//Properties
			//I need to initialize at least for 1 particle to avoid an if inside a loop appearing later
			main.Janus_pos.get(i).add(new ArrayList<Float>(0));		//Particles			
		}	
								
		BufferedReader br = null;
		try{
			br = new BufferedReader(new FileReader(route));
	    	String line = br.readLine();
	    	float size_p = 4.5f;
	    	
	    	//Read along file
	    	while (line != null) {
	    		String[] temp = line.split(" ");
	    		
	    		//Let's go to particle number. Are we getting data from a new particle?
	    		int part = Integer.valueOf(temp[0]);
	    		if (part>main.Janus_pos.get(0).size()-1){
	    			// If so, generate the subarrays to add their values
	    			for (int i = 0; i<main.Janus_pos.size();i++){
	    				main.Janus_pos.get(i).add(new ArrayList<Float>(0));		//Particles			
	    			}
	    		}
	    		
	    		//CM positions
	    		main.Janus_pos.get(0).get(part).add(Float.valueOf(temp[2])/size_p);
	    		main.Janus_pos.get(1).get(part).add(Float.valueOf(temp[3])/size_p);
	    		main.Janus_pos.get(2).get(part).add(Float.valueOf(temp[4])/size_p);
	    		
	    		//Rotation
	    		float theta, phi;
	    		Double x, y, z;
	    		x = Double.valueOf(temp[5]);
	    		y = Double.valueOf(temp[6]);
	    		z = Double.valueOf(temp[7]);
	    		
	    		phi = (float) Math.atan2(y,x);
	    		theta = (float) Math.atan2(Math.hypot(x, y),z);
	    		
	    		main.Janus_pos.get(3).get(part).add(theta);
	    		main.Janus_pos.get(4).get(part).add(phi);
	    		
	    		line = br.readLine();
	    	}
	    	//We suppose all particles are simulated for the same amount of time,
	    	//hence we set the maximum number of frames using the first particle
	    	main.t_max_ = main.Janus_pos.get(0).get(0).size()-1;
		}catch (Exception e){
			e.printStackTrace();
		}finally {
			try {
				//Close file i
				br.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/**
	 *  Load Janus positions.
	 *   
	 *  @param route (String). Route for Janus position particles.
	 *  @param num (int). Number of files (timesteps with config file)
	 *   
	 */	
	public static void load_janus(String route, int num){		
		int ind, part, i, j, junk_int, total_part;
		Double x, y, z, mx, my, mz, size_p, junk_double;
		
		//This array will contain all the data for particles
		main.Janus_pos = new ArrayList<ArrayList<ArrayList<Float>>>(0);
		
		//I structure it as 5 subarrays:
		//0: x |  1: y  |  2: z  |  3: theta  |  4: phi	
		//Then, at each subarray we will have different arrays. Each array is a different particle.
		//Hence you access for example as [0][0] which means [x][particle 1]
		for (i = 0; i<5;i++){
			main.Janus_pos.add(new ArrayList<ArrayList<Float>>(0));	//Properties		
		}				
		
		File folder = new File(route);
		File[] listOfFiles = folder.listFiles();	//No order guaranteed!
		Arrays.sort(listOfFiles);					//We order by name
		
		FileChannel fc;
		ByteBuffer byteBuffer;
		
		ind = -1;
		//Loop over all files
	    for (int file = 0; file < listOfFiles.length; file++) {
	    	//Only if we have a config file we read it
	    	if (listOfFiles[file].isFile() && listOfFiles[file].getName().startsWith("config.cds")) {
	    		ind++;
	    		if (ind==num) break;
				try{				
					fc = (FileChannel) Files.newByteChannel(Paths.get(listOfFiles[file].getAbsolutePath()), StandardOpenOption.READ);
		            byteBuffer = ByteBuffer.allocate((int)fc.size());
	                byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
	                fc.read(byteBuffer);
	                byteBuffer.flip();
			    	
	                total_part = byteBuffer.getInt();
	                // Only create them for the first file
	                if (ind == 0) {
	                	for (i = 0; i<main.Janus_pos.size();i++){
	                		for (j=0; j<total_part;j++) {
		                		main.Janus_pos.get(i).add(new ArrayList<Float>(0));		//Particles			
	                		}
	                	}
	                }
	                
	                for (i =0; i< total_part; i++) {
		                part = byteBuffer.getInt()-1;
		                for (i=0; i<31; i++) {
		                	junk_int = byteBuffer.getInt();
		                }
		                
		                size_p = byteBuffer.getDouble();	//a0
		                size_p = byteBuffer.getDouble();	//ah

		                x = byteBuffer.getDouble();
		                y = byteBuffer.getDouble();
		                z = byteBuffer.getDouble();

		                for (i=0; i<9; i++) {
		                	junk_double = byteBuffer.getDouble();
		                }
		                
		                mx = byteBuffer.getDouble();
		                my = byteBuffer.getDouble();
		                mz = byteBuffer.getDouble();
		                
		                for (i=0; i<31; i++) {
		                	junk_double = byteBuffer.getDouble();
		                }
		                
		                byteBuffer.clear();

		            	fc.close();
	            	
			    		//CM positions
			    		main.Janus_pos.get(0).get(part).add((float) (x/size_p));
			    		main.Janus_pos.get(1).get(part).add((float) (y/size_p));
			    		main.Janus_pos.get(2).get(part).add((float) (z/size_p));
			    		
			    		//Rotation
			    		float theta, phi;
			    		
			    		phi = (float) Math.atan2(my,mx);
			    		theta = (float) Math.atan2(Math.hypot(mx, my),mz);
			    		
			    		main.Janus_pos.get(3).get(part).add(theta);
			    		main.Janus_pos.get(4).get(part).add(phi);		
	                }
	                
	                byteBuffer.clear();
	            	fc.close();

	            		            	
				}catch (Exception e){
					e.printStackTrace();
				}finally {
					
				}
	    	}
		}
	    	
	    //We suppose all particles are simulated for the same amount of time,
	    //hence we set the maximum number of frames using the first particle
	    main.t_max_ = main.Janus_pos.get(0).get(0).size()-1;
	}
	
	/**
	 *  Load the phoretic field.
	 *  
	 *  Notice that in recent versions of Ludwig, the decomposition might be 1,1,1 even if you parallelized the simulation with sub-boxes.
	 *     
	 *  @param route (String). Route for Ludwig's output will all the phi-*.001-001 files.
	 *  @param num (int). Number of files (timesteps with phi file)
	 *  @param PEx (int). Number of nodes used for the simulation in X direction.
	 *  @param PEy (int). Number of nodes used for the simulation in Y direction.
	 *  @param PEz (int). Number of nodes used for the simulation in Z direction.
	 *  @param ll (boolean). Does the phoretic field have 2 components? Otherwise consider only 1 component.
	 *  @param ll_O2 (boolean). If it has 2 components, True represents the first component, False, the second.
	 *  @param load_max_min (boolean). Adjust values for scale bar to maximum-minimum taken from the data.

	 */	
	public static void load_phi(String route, int num, int PEx, int PEy, int PEz, boolean ll, boolean ll_O2, boolean load_max_min) throws IOException {
		int nlocal[] = new int[3];	//Ludwig's nlocal structure
	    int zone, nox, noy, noz, i, j, k, ic_g, jc_g, kc_g, index_l, index_g, index, lnodes;	//Ludwig's indexes structure

		int orders = 1;
		if (ll) orders = 2;
		
		double phi[] = new double[main.NX*main.NY*main.NZ*orders];	//Phoretic field
		
	    int ind;
		float min, max;

	    //Number of points per sub-box
		nlocal[0] = main.NX/PEx;
		nlocal[1] = main.NY/PEy;
		nlocal[2] = main.NZ/PEz;
		lnodes=nlocal[0]*nlocal[1]*nlocal[2];

		min = 9E20f;
		max = -9E20f;
		
		File folder = new File(route);
		File[] listOfFiles = folder.listFiles();	//No order guaranteed!
		Arrays.sort(listOfFiles);					//We order by name
		
		FileChannel fc;
		ByteBuffer byteBuffer;
		Buffer buffer;
		
		main.boxCol_t = new float[num][main.NX*main.NY*main.NZ];
		ind = -1;
		//Loop over all files
	    for (int file = 0; file < listOfFiles.length; file++) {
	    	//Only if we have a phi file we read it
	    	if (listOfFiles[file].isFile() && listOfFiles[file].getName().startsWith("phi-")) {
	    		ind++;
	    		if (ind==num) break;
				try{				
					fc = (FileChannel) Files.newByteChannel(Paths.get(listOfFiles[file].getAbsolutePath()), StandardOpenOption.READ);
		            byteBuffer = ByteBuffer.allocate((int)fc.size());
	                byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
	                fc.read(byteBuffer);
	                byteBuffer.flip();
			    	
	                buffer = byteBuffer.asDoubleBuffer();
	                ((DoubleBuffer)buffer).get(phi);
	                byteBuffer.clear();

	            	fc.close();
	            	
				    //Then I reorder the phis because of the way Ludwig writes the data (PARALLEL)				 
			    	zone=0;
			    	for(nox=0;nox<PEx;nox++){
			    		for(noy=0;noy<PEy;noy++){
			    			for(noz=0;noz<PEz;noz++){
			    				zone+=1;
			    				for(i=0;i<nlocal[0];i++) {
			    					ic_g = nox*nlocal[0] + i;
			    					for(j=0;j<nlocal[1];j++) {
			    						jc_g = noy*nlocal[1] + j;
			    						for(k=0;k<nlocal[2];k++) {
			          						kc_g = noz*nlocal[2] + k;
			    							index_l = k + j*nlocal[2] + i*nlocal[1]*nlocal[2];
			    							index_g = kc_g + jc_g*main.NZ + ic_g*main.NY*main.NZ;
			    							index = index_l+(zone-1)*lnodes;
			    							if (ll){
			    								if (ll_O2) {
			    									main.boxCol_t[ind][index_g]=(float) phi[2*index];	
			    								}else {
			    									main.boxCol_t[ind][index_g]=(float) phi[2*index+1];
			    								}			    								
			    							}else{
			    								main.boxCol_t[ind][index_g]=(float) phi[index];
			    							}
			    							if (main.boxCol_t[ind][index_g]>max) max = (float) main.boxCol_t[ind][index_g];
			    							if (main.boxCol_t[ind][index_g]<min) min = (float) main.boxCol_t[ind][index_g];
			    						}
			    					}
			    				}
			    			}	
			    		}			    		
						//index = k + j*NZ + i*NY*NZ;				    	
			    	}
			    	
				}catch (Exception e){
					e.printStackTrace();
				}finally {
					
				}
	    	}
		}
	    if (!load_max_min) {
	    	main.phi_max_value=max;
	    	main.phi_min_value=min;
			main.phi_zero_value = (max-min)/2+min;
	    }
	}

	/**
	 *  Load the velocity field.
	 *  
	 *  Notice that in recent versions of Ludwig, the decomposition might be 1,1,1 even if you parallelized the simulation with sub-boxes.
	 *     
	 *  @param route (String). Route for Ludwig's output will all the vel-*.001-001 files.
	 *  @param num (int). Number of files (timesteps with vel file)
	 *  @param PEx (int). Number of nodes used for the simulation in X direction.
	 *  @param PEy (int). Number of nodes used for the simulation in Y direction.
	 *  @param PEz (int). Number of nodes used for the simulation in Z direction.
	 *  @param multiplier (float). Multiply the velocities per this quantity.

	 */	
public static void load_vel(String route, int num, int PEx, int PEy, int PEz, float multiplier) throws IOException {
	int nlocal[] = new int[3];	//Ludwig's nlocal structure
    int zone, nox, noy, noz, i, j, k, ic_g, jc_g, kc_g, index_l, index_g, index, lnodes;	//Ludwig's indexes structure
	
	double vel[] = new double[main.NX*main.NY*main.NZ*3];	//vel field
	
    int ind;
	float min, max;
	
    //Number of points per sub-box
	nlocal[0] = main.NX/PEx;
	nlocal[1] = main.NY/PEy;
	nlocal[2] = main.NZ/PEz;
	lnodes=nlocal[0]*nlocal[1]*nlocal[2];

	min = 9E20f;
	max = -9E20f;
	
	File folder = new File(route);
	File[] listOfFiles = folder.listFiles();	//No order guaranteed!
	Arrays.sort(listOfFiles);					//We order by name
	
	FileChannel fc;
	ByteBuffer byteBuffer;
	Buffer buffer;
	
	main.boxCol_t_vel = new float[num][main.NX*main.NY*main.NZ*3];
	ind = -1;
	//Loop over all files
    for (int file = 0; file < listOfFiles.length; file++) {
    	//Only if we have a vel file we read it
    	if (listOfFiles[file].isFile() && listOfFiles[file].getName().startsWith("vel-")) {
    		ind++;
    		if (ind==num) break;
			try{				
				fc = (FileChannel) Files.newByteChannel(Paths.get(listOfFiles[file].getAbsolutePath()), StandardOpenOption.READ);
	            byteBuffer = ByteBuffer.allocate((int)fc.size());
                byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
                fc.read(byteBuffer);
                byteBuffer.flip();
		    	
                buffer = byteBuffer.asDoubleBuffer();
                ((DoubleBuffer)buffer).get(vel);
                byteBuffer.clear();

            	fc.close();
            	
			    //Then I reorder the vels because of the way Ludwig writes the data (PARALLEL)				 
		    	zone=0;
		    	for(nox=0;nox<PEx;nox++){
		    		for(noy=0;noy<PEy;noy++){
		    			for(noz=0;noz<PEz;noz++){
		    				zone+=1;
		    				for(i=0;i<nlocal[0];i++) {
		    					ic_g = nox*nlocal[0] + i;
		    					for(j=0;j<nlocal[1];j++) {
		    						jc_g = noy*nlocal[1] + j;
		    						for(k=0;k<nlocal[2];k++) {
		          						kc_g = noz*nlocal[2] + k;
		    							index_l = k + j*nlocal[2] + i*nlocal[1]*nlocal[2];
		    							index_g = kc_g + jc_g*main.NZ + ic_g*main.NY*main.NZ;
		    							index = index_l+(zone-1)*lnodes;
		    							for (int n=0; n<3; n++) {
		    								main.boxCol_t_vel[ind][3*index_g+n]=(float) ((float) multiplier*vel[3*index+n]);
		    							}
		    							if (main.boxCol_t_vel[ind][index_g]>max) max = (float) main.boxCol_t[ind][index_g];
		    							if (main.boxCol_t_vel[ind][index_g]<min) min = (float) main.boxCol_t[ind][index_g];
		    						}
		    					}
		    				}
		    			}	
		    		}			    		
					//index = k + j*NZ + i*NY*NZ;				    	
		    	}
		    	
			}catch (Exception e){
				e.printStackTrace();
			}finally {
				
			}
    	}
	}
   	main.vel_max_value=max;
   	main.vel_min_value=min;
	main.vel_zero_value = (max-min)/2+min;
	}
}
