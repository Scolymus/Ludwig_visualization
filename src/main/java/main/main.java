package main;

/*######################################################
#          			LUDWIG VISUALIZER     		       #  
#                                                      #  
#        Author: Scolymus                              #
#        License: CC BY-NC-SA 4.0                      #
#                                                      #
#------------------------------------------------------#*/

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;

import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.GL4;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.util.FPSAnimator;

import main.Maths;
import main.Shaders_newProgram.ObjectType;

import javax.swing.JFrame;
import javax.swing.Timer;

 /**
 * inspired from http://www.lighthouse3d.com/cg-topics/code-samples/opengl-3-3-glsl-1-5-sample/
 * 
 */
public class main implements GLEventListener {
 
	/* ********************************************************************** **
	 * 																		   *
	 * 									SHADERS					   			   *
	 *                                       						   	       *
	 * ********************************************************************** */
	
	// Program
	int programID_janus, programID_box, programID_vel;
	 
	// Vertex Attribute Locations
	int vertexLoc[], colorLoc[];

	static int colorBox;

	// Uniform variable Locations
	int projMatrixLoc[], viewMatrixLoc[], phi_max, phi_min, phi_zero, phi_Radius, phi_janus_loc;
		
	// storage for Matrices
	float projMatrix[] = new float[16];
	float viewMatrix[] = new float[16];
	float viewMatrix_temp[] = new float[16];
	
	// Values for VAOs
	protected static int triangleVAO, axisVAO, boxVAO, wallsxVAO, wallsyVAO, wallszVAO;

	//Type of shader for phi visualization
	static int type_phi;
		
	/* ********************************************************************** **
	 * 																		   *
	 * 						VERTEXS: POS AND COLORS				   			   *
	 *                                       						   	       *
	 * ********************************************************************** */
	
	//___________________________________________________________________________
	//
	//								GENERAL AXIS
	//___________________________________________________________________________
	
	/**
	 * General axis x,y,z
	 */
	static float verticesAxis[] = { 
			-1.0f,  0.0f, 0.0f, 1.0f, 
			 1.0f,  0.0f, 0.0f, 1.0f,
			 0.0f, -1.0f, 0.0f, 1.0f, 
			 0.0f,  1.0f, 0.0f, 1.0f,
			 0.0f,  0.0f, -1.0f, 1.0f, 
			 0.0f,  0.0f, 1.0f, 1.0f}; 
	/**
	 * Color for general axis x,y,z
	 */
	static float colorAxis[] = { 
			1.0f, 1.0f, 0.0f, 0.0f, 
			1.0f, 0.0f, 0.0f, 0.0f, 
			0.0f, 1.0f, 1.0f, 0.0f, 
			0.0f, 1.0f, 0.0f, 0.0f, 
			1.0f, 0.0f, 1.0f, 0.0f, 
			0.0f, 0.0f, 1.0f, 0.0f
			};
	
	//___________________________________________________________________________
	//
	//									JANUS
	//___________________________________________________________________________
	
	/**
	 *  Janus vertexes
	 */
	static float janusv[];
	/**
	 *  Janus colors
	 */
	static float janusc[];
	/**
	 * 	Janus positions
	 */
	static ArrayList<ArrayList<ArrayList<Float>>> Janus_pos;
	
	static float radius_particle;
	/**
	 * 	Do we draw particles??? or field???
	 */
	static boolean particles;
	static boolean field;
	//___________________________________________________________________________
	//
	//								  PHI BOX
	//___________________________________________________________________________
	
	/**
	 *  Phi box positions
	 */
	static float boxAxis[];
	//static float boxCol[];
	/**
	 * 	Phi colors (values of phi)-
	 *  [i][j]: i time step. j vertex
	 */
	static float boxCol_t[][];
	
	static int total_steps;
	
	static int NX, NY, NZ;
	
	static boolean ll_O2;
	
	//___________________________________________________________________________
	//
	//								  VEL BOX
	//___________________________________________________________________________
	
	static float boxCol_t_vel[][];
	
	static boolean vel_fluid;
	
	
	//___________________________________________________________________________
	//
	//								   WALLS
	//___________________________________________________________________________
	
	/**
	 *  Walls positions
	 */
	static boolean[] walls;
	
	/**
	 *  Wall vertexes
	 *  x-min, x-max, y-min, y-max, z-min, z-max
	 */
	static float wallsv[][];

	/**
	 * 	Walls colors 
	 */
	static float wallsc[];
	
	
	/* ********************************************************************** **
	 * 																		   *
	 * 						MOUSE AND KEYBOARD CONTROL						   *
	 *                                       						   	       *
	 * ********************************************************************** */

	
	private static float rotateX = 0;   // rotation amounts about axes, controlled by keyboard
	private static float rotateY = 0;
	private static float m_X_old = 0;   // rotation amounts about axes, controlled by keyboard
	private static float m_Y_old = 0;	
	private static float m_X = 0;	
	private static float m_Y = 0;	
	
	// horizontal angle : toward -Z
	float horizontalAngle = 3.14f;
	// vertical angle : 0, look at the horizon
	float verticalAngle = 0.0f;
	// Initial Field of View
	float initialFoV = 45.0f;
	
	public enum dirc{X_BASE, X_TOP, Y_BASE, Y_TOP, Z_BASE, Z_TOP};
	
	static float speed = 3.0f; // 3 units / second
	float mouseSpeed = 0.005f;
	static float[] position = { 1, 0, 0 };
	static float[] direction = { -1, 0, 0 };
	static float[] right = { 0, 1, 0 };
	static float[] up = { 0, 1, 0 };
	static float FOV = 45;
	static float ratio;	
	static int fps = 60;
	double currentTime = 0;;
	double lastTime = 0;
	static float deltaTime;
	static float phi_max_value;
	static float phi_min_value;
	static float phi_zero_value;
	static float phi_max_radius;
	static float vel_max_value;
	static float vel_min_value;
	static float vel_zero_value;
	static float[] phi_loc_janus;

    Robot robot;
    static JFrame framee;
	public static GLAutoDrawable gg;
    private FPSAnimator animator;

    /** Min && Time step. */
    private int t = 0;
    int tt = 0;
    /** Max Time step. */
    protected static int t_max_ = 0;
  
	private static boolean dragging = false;  // is a drag operation in progress?

	// ------------------
 
    //https://stackoverflow.com/questions/19332668/drawing-the-axis-with-its-arrow-using-opengl-in-visual-studio-2010-and-c

	/**
	 *  Run this software!
	 *  Change the firsts parameters until "DON'T MODIFY AFTER THIS LINE". Then, run the software.
	 */
	public static void main(String[] args) {
		// allocate the openGL application
		main sample = new main();
		
		//CHANGE DATA
		//Routes to files
		//if there are no particles, it doesn't matter route_pos. Just put something random but choose particles = false!!!!
		//In ubuntu, route is /media/... Between folder: /
		//In windows, route is C:\\.... Between folder: \\
		//Do not end the route string with a / or \\
		String route_pos = "C:\\folder\\subfolder\\your_simulation";
		String route_phi = route_pos;
		String route_vel = route_phi;

		//Simulation input properties
		int time_steps = 300;	//Number of files printed for config or phi. I supposed you printed with the same frequency...
		NX = 64;	//Number of nodes per direction
		NY = 32;
		NZ = 32;
		int PX = 1;	//Processor decomposition. In new Ludwig's version can be 1,1,1 even if your simulation was run in parallel.
		int PY = 1;
		int PZ = 1;		
		boolean ll = true;	//True: 2 orders parameter. False: 1 order parameter in phi files.
		radius_particle = 4.5f;	// I supposed all particles have the same size... otherwise you'll need to change 

		//Visualization properties		
		fps = 300;	// How fast the video is processed with OPENGL
		particles = true;	//true: load and show particles
		float portion_janus = 0.5f;	//Particles will be coloured in 2 colours. How much is painted with one color is this number.
									//0.5f is janus. 0.0f or 1.0f would be all the same colour
		field = true;	//true: load and show the phi field.
		ll_O2 = true;	//If we have 2 order parameters, show first or second parameter.
		vel_fluid = false;	//true: load vel fluid	NOT IMPLEMENTED YET!!!		
		float multiplier_vel = 5.f;	//quantity to multiply all vel fluid components
		
		walls = new boolean[6];	//Draw walls at. It simple draws a solid color rectangle.
		walls[dirc.X_BASE.ordinal()] = false;	//x min
		walls[dirc.X_TOP.ordinal()] = false;	//x max
		walls[dirc.Y_BASE.ordinal()] = false;	//y min
		walls[dirc.Y_TOP.ordinal()] = false;	//y max
		walls[dirc.Z_BASE.ordinal()] = true;	//z min
		walls[dirc.Z_TOP.ordinal()] = false;	//z max
		//Still not implemented rigth and up for "to" different than -1
		Init_load.camera_start(dirc.Z_TOP.ordinal(), -1);
		
		//WATCH OUT!!!! IF YOU HAVE MORE THAN 1 PARTICLES MODIFY THE PHI_VERTEX_SPHERE_MULT.FRAGMENT
		//DO I NEED TO SEND A TEXTYRE INSTEAD OF UNIFORMS? THERE ARE LIMITS FOR UNIFORMS!!!
		//0: All.	1: Only around sphere	2:	Only around spheres but to be used with many spheres
		type_phi = 1;
		phi_max_radius = 3.f;	//Max radius of points to see around a sphere
		boolean static_values_max_min = false;	//Set true and modify parameters if you want the following as min and max values!!!		
		if (static_values_max_min) {
			phi_max_value = 0.5f; //1.31f
			phi_min_value = 0;
			phi_zero_value = (phi_max_value-phi_min_value)/2+phi_min_value;
		}
		
		//########### DON'T MODIFY AFTER THIS LINE #########################
		
		//DATA FOR JANUS
		Init_load.generate_vertexes_janus(32, 32, 0.5f, portion_janus);
		if (particles) {
			Init_load.load_janus(route_pos, time_steps);
			//Init_load.load_janus_old(route_pos);
			phi_loc_janus = new float[Janus_pos.get(0).size()*3];
			total_steps = Janus_pos.get(0).get(0).size();
		}
		
		//DATA FOR PHI
		if (field || vel_fluid) {
			Init_load.generate_vertexes_phi(NX,NY,NZ);
		}
		
		if (field) {
			long timetime = System.nanoTime();
			try {
				Init_load.load_phi(route_phi, time_steps, PX, PY, PZ, ll, ll_O2,static_values_max_min);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
			if (!ll_O2 ) {
				if (type_phi > 0) {
					System.out.println("This type_phi hasn't been implemented yet. For O2 there are models 0,1,2. For oil/water just 0. It will be used model 0 for O2");
				}
				type_phi = 100;
			}
			System.out.println((timetime-System.nanoTime())*1.0/1000000000.);
			total_steps = boxCol_t.length;
		}
		
		if (vel_fluid) {
			long timetime = System.nanoTime();
			try {
				Init_load.load_vel(route_vel, time_steps, PX, PY, PZ, multiplier_vel);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
			System.out.println((timetime-System.nanoTime())*1.0/1000000000.);
			total_steps = boxCol_t.length;
		}
		
		//DATA FOR WALLS
		Init_load.generate_vertexes_walls();
		
		// allocate a frame and display the openGL inside it		
		framee = newJFrame("JOGL3 sample with Shader",
				sample, 10, 10, 700, 700);

		// display it and let's go
		framee.setVisible(true);
	}
    
	/**
	 *  Create the window that will contain the OPENGL.
	 *  @param name (String). Name of the window (for OS window)
	 *  @param sample (GLEventListener). OPENGL listener
	 *  @param x (int). Set window at x position of your screen.
	 *  @param y (int). Set window at y position of your screen.
	 *  @param w (int). Set the width of your window.
	 *  @param h (int). Set the height of your window. 
	 *  
	 */
	public static JFrame newJFrame(String name, GLEventListener sample, int x,
			int y, int width, int height) {
			// Create frame, set location and behavior at closing.
			JFrame frame = new JFrame(name);
			frame.setBounds(x, y, width, height);
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	 
			// Configure OPENGL to GL4 version.
			GLProfile glp = GLProfile.get(GLProfile.GL4);
			GLCapabilities glCapabilities = new GLCapabilities(glp);
			final GLCanvas glCanvas = new GLCanvas(glCapabilities);
	 
			// Add listeners
			glCanvas.addGLEventListener(sample);
			
			//Mouse listeners
			//http://math.hws.edu/eck/cs424/notes2013/opengl-jogl/JoglTemplate.java
			//http://www.opengl-tutorial.org/beginners-tutorials/tutorial-6-keyboard-and-mouse/
			glCanvas.addMouseWheelListener(new MouseWheelListener(){

				@Override
				public void mouseWheelMoved(MouseWheelEvent arg0) {
					// TODO Auto-generated method stub
					FOV = 45 - 5 * arg0.getWheelRotation();
				}
				
			});
			
			glCanvas.addMouseListener(new MouseListener(){

				@Override
				public void mousePressed(MouseEvent arg0) {
					// TODO Auto-generated method stub
					if (dragging) {
						return;  // don't start a new drag while one is already in progress
					}
					m_X_old = arg0.getX();
					m_Y_old = arg0.getY();
					// TODO: respond to mouse click at (x,y)
					dragging = true;  // might not always be correct!
				}

				@Override
				public void mouseReleased(MouseEvent arg0) {
					// TODO Auto-generated method stub
					if (! dragging) {
						return;
					}
					dragging = false;
					//m_X = framee.getWidth()/2;
					//m_Y = framee.getHeight()/2;
					m_X = 0.f;
					m_Y = 0.f;
				}

				@Override
				public void mouseClicked(MouseEvent arg0) {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void mouseEntered(MouseEvent arg0) {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void mouseExited(MouseEvent arg0) {
					// TODO Auto-generated method stub
					
				}
			});
			
			glCanvas.addMouseMotionListener(new MouseMotionListener(){

				@Override
				public void mouseDragged(MouseEvent arg0) {
					// TODO Auto-generated method stub
					if (! dragging) {
						return;
					}
					m_X = -arg0.getX()+m_X_old;
					m_Y = -arg0.getY()+m_Y_old;
				}

				@Override
				public void mouseMoved(MouseEvent arg0) {
					// TODO Auto-generated method stub
					//These values have the origin in the frame. Not in the screen.
					//m_X = arg0.getX();	
					//m_Y = arg0.getY();	
					//System.out.println("Dx: "+m_X+"  y: "+m_Y + "  f_x"+framee.getX());
				}
				
			});
			
			// keyboard listeners
			glCanvas.addKeyListener(new KeyListener(){

				@Override
				public void keyPressed(KeyEvent e) {
					// TODO Auto-generated method stub
					if (e.getKeyCode()==KeyEvent.VK_LEFT){
					    position[0] -= right[0] * deltaTime * speed;
					    position[1] -= right[1] * deltaTime * speed;
					    position[2] -= right[2] * deltaTime * speed;
						//rotateX -= 15;
					}else if (e.getKeyCode()==KeyEvent.VK_RIGHT){
					    position[0] += right[0] * deltaTime * speed;
					    position[1] += right[1] * deltaTime * speed;
					    position[2] += right[2] * deltaTime * speed;
						//rotateX += 15;
					}else if (e.getKeyCode()==KeyEvent.VK_UP){
					    position[0] += direction[0] * deltaTime * speed;
					    position[1] += direction[1] * deltaTime * speed;
					    position[2] += direction[2] * deltaTime * speed;
						//rotateY += 15;
					}else if (e.getKeyCode()==KeyEvent.VK_DOWN){
					    position[0] -= direction[0] * deltaTime * speed;
					    position[1] -= direction[1] * deltaTime * speed;
					    position[2] -= direction[2] * deltaTime * speed;

						//rotateY -= 15;
					}else if (e.getKeyCode()==KeyEvent.VK_W){
						
					}else if (e.getKeyCode()==KeyEvent.VK_S){

					}else if (e.getKeyCode()==KeyEvent.VK_A){

					}else if (e.getKeyCode()==KeyEvent.VK_D){

					}
					//System.out.println("Rx: "+position[0]+"  y: "+position[1]);
					glCanvas.display();
				}

				@Override
				public void keyReleased(KeyEvent e) {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void keyTyped(KeyEvent e) {
					// TODO Auto-generated method stub
					
				}
				
			});
			
			frame.add(glCanvas);
			rotateX = 0;  // initialize some variables used in the drawing.
			rotateY = 0;
			
			// TODO:  Uncomment the next two lines to enable keyboard event handling
			frame.requestFocusInWindow();
			
			return frame;
		}
	
	/** GL Init */
	@Override
	public void init(GLAutoDrawable drawable) {
		GL4 gl = drawable.getGL().getGL4();
		gg = drawable;
		gl.glEnable(GL4.GL_DEPTH_TEST);
		gl.glBlendFunc(GL4.GL_SRC_ALPHA, GL4.GL_ONE_MINUS_SRC_ALPHA);
		gl.glEnable( GL4.GL_BLEND );

		gl.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);	// Background color
		gl.glPointSize(10);

		//init variables size
		vertexLoc = new int[3];
		colorLoc = new int[3];
		projMatrixLoc = new int[3];
		viewMatrixLoc = new int[3];
		
		//Creates shader programs. First for janus and axis.
		Shaders_newProgram janus = new Shaders_newProgram(gl, ObjectType.Janus, 0);
		programID_janus = janus.Program_ID;
		vertexLoc[0] = janus.vertexLoc;
		colorLoc[0] = janus.colorLoc;
 
		projMatrixLoc[0] = janus.projMatrixLoc;
		viewMatrixLoc[0] = janus.viewMatrixLoc;
		gl = janus.gl;	//Update gl. Value in java is sent byVal, not byRef
		
		//Then for phi box
		if (field) {
			Shaders_newProgram box = new Shaders_newProgram(gl, ObjectType.Phi_field, type_phi);
			programID_box = box.Program_ID;
			vertexLoc[1] = box.vertexLoc;
			colorLoc[1] = box.colorLoc;
	 
			projMatrixLoc[1] = box.projMatrixLoc;
			viewMatrixLoc[1] = box.viewMatrixLoc;
	 
			phi_max = box.phi_max;
			if (!ll_O2) {
				phi_min = box.phi_min;
				phi_zero = box.phi_zero;
			}
			phi_Radius = box.Radius;
			phi_janus_loc = box.SpheresLoc;
			
			gl = box.gl;
		}
		
		//Then for vel box
		if (vel_fluid) {
			Shaders_newProgram box = new Shaders_newProgram(gl, ObjectType.Vel_field, 0);
			programID_vel = box.Program_ID;
			vertexLoc[2] = box.vertexLoc;
			colorLoc[2] = box.colorLoc;
	 
			projMatrixLoc[2] = box.projMatrixLoc;
			viewMatrixLoc[2] = box.viewMatrixLoc;
	 		
			gl = box.gl;
		}		
		
		try {
			robot = new Robot();
		} catch (AWTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		lastTime = System.currentTimeMillis();
		//m_X = framee.getWidth()/2;
		//m_Y = framee.getHeight()/2;
		
		animator = new FPSAnimator(drawable, fps);
        animator.start();

	}
	
	/**
	 *  Render the scene.
	 *  @param gl (GL4). GL where to work.
	 */
	protected void renderScene(GL4 gl) {
		// Calculate time to render
		currentTime = System.currentTimeMillis();
		deltaTime = (float) (currentTime - lastTime)/1000;

		// Reset OPENGL and redraw camera.
		gl.glClear(GL4.GL_COLOR_BUFFER_BIT | GL4.GL_DEPTH_BUFFER_BIT);

		horizontalAngle += mouseSpeed * deltaTime * ( m_X);
		verticalAngle   += mouseSpeed * deltaTime * ( m_Y);

		direction[0] = (float) (Math.cos(verticalAngle) * Math.sin(horizontalAngle));
		direction[1] = (float) (Math.sin(verticalAngle));
		direction[2] = (float) (Math.cos(verticalAngle) * Math.cos(horizontalAngle));

		right[0] = (float) (Math.sin(horizontalAngle));
		right[1] = 0.f;
		right[2] = (float) (Math.cos(horizontalAngle));

		Maths.crossProduct(right, direction, up);
		this.projMatrix = Maths.buildProjectionMatrix(FOV, ratio, 1.0f, 1000.0f, this.projMatrix);
		
		Maths.setCamera(	position[0], position[1], position[2],
					position[0]+direction[0], position[1]+direction[1], position[2]+direction[2],
					this.viewMatrix, rotateX, rotateY);

		// Set the program to draw. This OPENGL program includes particles and/or fields and/or walls
		gl.glUseProgram(this.programID_janus);
 
		// must be called after glUseProgram
		// set the view and the projection matrix 
		gl.glUniformMatrix4fv( this.projMatrixLoc[0], 1, false, this.projMatrix, 0);
    	//gl.glUniformMatrix4fv(this.viewMatrixLoc, 1, false, this.viewMatrix, 0);
		   
	    t++;	//time step
	    
	    //if (tt==boxCol_t.length) tt= 0;
	    if (tt==total_steps) tt= 0;	// Reset to time zero if we were crossing the limit of time steps
	    
	    // Draw particles if we need to
	    if (particles) {
		    if (t==Janus_pos.get(0).get(0).size()){
		    	t=0;
		    }		   
		    // Use the vertexes for particles
		    gl.glBindVertexArray(main.triangleVAO);
		    
		    // For each particle:
		    for (int i = 0; i<Janus_pos.get(0).size(); i++){
		    	viewMatrix_temp = new float[16];
		    	phi_loc_janus[i*3] = Janus_pos.get(0).get(i).get(tt);
		    	phi_loc_janus[i*3+1] = Janus_pos.get(1).get(i).get(tt);
		    	phi_loc_janus[i*3+2] = Janus_pos.get(2).get(i).get(tt);
		    	
		    	// Rotate and translate matrix
		    	Maths.setRTMatrix(this.viewMatrix_temp, Janus_pos.get(3).get(i).get(tt), Janus_pos.get(4).get(i).get(tt), true, 
		    									   Janus_pos.get(0).get(i).get(tt), Janus_pos.get(1).get(i).get(tt), Janus_pos.get(2).get(i).get(tt));

				viewMatrix_temp = Maths.multMatrixf(viewMatrix, viewMatrix_temp);
				gl.glUniformMatrix4fv( this.viewMatrixLoc[0], 1, false, viewMatrix_temp, 0);
				gl.glDrawArrays(GL4.GL_TRIANGLE_STRIP, 0, janusv.length/4);
				
		    }

			int error2 = gl.glGetError();
			if(error2!=0){
				System.err.println("ERROR on render 0 : " + error2);}
		    
	    	gl.glUniformMatrix4fv(this.viewMatrixLoc[0], 1, false, this.viewMatrix, 0);
	    }		   
    	
    	//Axis
    	gl.glBindVertexArray(main.axisVAO);
		gl.glDrawArrays(GL4.GL_LINES, 0, 6);
		
		//Walls
		gl.glBindVertexArray(main.wallsxVAO);
		
		for (int i = 0; i<2; i++){
			if (walls[i] == true){
		    	viewMatrix_temp = new float[16];
				Maths.setRTMatrix(this.viewMatrix_temp, 0, 0, true, NX*i/radius_particle, 0, 0);

				viewMatrix_temp = Maths.multMatrixf(viewMatrix, viewMatrix_temp);
				gl.glUniformMatrix4fv( this.viewMatrixLoc[0], 1, false, viewMatrix_temp, 0);
				gl.glDrawArrays(GL4.GL_TRIANGLE_FAN, 0, wallsv[0].length/4);
			}
		}		

		gl.glBindVertexArray(main.wallsyVAO);
		
		for (int i = 0; i<2; i++){
			if (walls[i+2] == true){
		    	viewMatrix_temp = new float[16];
				Maths.setRTMatrix(this.viewMatrix_temp, 0, 0, true, 0, NY*i/radius_particle, 0);

				viewMatrix_temp = Maths.multMatrixf(viewMatrix, viewMatrix_temp);
				gl.glUniformMatrix4fv( this.viewMatrixLoc[0], 1, false, viewMatrix_temp, 0);
				gl.glDrawArrays(GL4.GL_TRIANGLE_FAN, 0, wallsv[1].length/4);
			}
		}	
		
		gl.glBindVertexArray(main.wallszVAO);
		
		for (int i = 0; i<2; i++){
			if (walls[i+4] == true){
		    	viewMatrix_temp = new float[16];
				Maths.setRTMatrix(this.viewMatrix_temp, 0, 0, true, 0, 0, NZ*i/radius_particle);

				viewMatrix_temp = Maths.multMatrixf(viewMatrix, viewMatrix_temp);
				gl.glUniformMatrix4fv( this.viewMatrixLoc[0], 1, false, viewMatrix_temp, 0);
				gl.glDrawArrays(GL4.GL_TRIANGLE_FAN, 0, wallsv[2].length/4);
			}
		}	
		int error = gl.glGetError();
		if(error!=0){
			System.err.println("ERROR on render 1 : " + error);}
		
	    // Draw order parameter field if we need to
    	if (field) {
			gl.glUseProgram(this.programID_box);
			gl.glUniformMatrix4fv( this.projMatrixLoc[1], 1, false, this.projMatrix, 0);
	    	gl.glUniformMatrix4fv(this.viewMatrixLoc[1], 1, false, this.viewMatrix, 0);
	    	if (!ll_O2) {
	    		gl.glUniform1f(this.phi_min, phi_min_value);
	    		gl.glUniform1f(this.phi_zero, phi_zero_value);
	    	}
	    	gl.glUniform1f(this.phi_max, phi_max_value);    	
	    	gl.glUniform1f(this.phi_Radius, phi_max_radius);
	    	if (particles) gl.glUniform3fv(this.phi_janus_loc, Janus_pos.get(0).size(), Buffers.newDirectFloatBuffer(phi_loc_janus));
    	
    		gl.glBindVertexArray(main.boxVAO);		

    		gl.glBindBuffer(GL4.GL_ARRAY_BUFFER, main.colorBox);
    		gl.glBufferData(GL4.GL_ARRAY_BUFFER, boxCol_t[0].length * Float.SIZE / 8, Buffers.newDirectFloatBuffer(boxCol_t[tt]), GL4.GL_STATIC_DRAW);
    		//glBufferSubData
    		gl.glBindBuffer(GL4.GL_ARRAY_BUFFER, 0);

    		gl.glDrawArrays(GL4.GL_POINTS, 0, boxAxis.length/4);				
    	}

	    // Draw vel field if we need to
    	if (vel_fluid) {
			gl.glUseProgram(this.programID_vel);
			gl.glUniformMatrix4fv( this.projMatrixLoc[2], 1, false, this.projMatrix, 0);
	    	gl.glUniformMatrix4fv(this.viewMatrixLoc[2], 1, false, this.viewMatrix, 0);
   	
    		gl.glBindVertexArray(main.boxVAO);		

    		gl.glBindBuffer(GL4.GL_ARRAY_BUFFER, main.colorBox);
    		gl.glBufferData(GL4.GL_ARRAY_BUFFER, boxCol_t[0].length * Float.SIZE / 8, Buffers.newDirectFloatBuffer(boxCol_t[tt]), GL4.GL_STATIC_DRAW);
    		//glBufferSubData
    		gl.glBindBuffer(GL4.GL_ARRAY_BUFFER, 0);

    		gl.glDrawArrays(GL4.GL_POINTS, 0, boxAxis.length/4);				
    	}
    	
		tt++;
		//robot.mouseMove(framee.getX()+framee.getWidth()/2, framee.getY()+framee.getHeight()/2);
		
		// Check out error
		error = gl.glGetError();
		if(error!=0){
			System.err.println("ERROR on render : " + error);}
		lastTime = currentTime;
		//System.out.println("ha: "+horizontalAngle+"  hv: "+verticalAngle+" milis: "+deltaTime);
		
	}
 
	/*  Change window size. Set the camera.
	 *  @param gl (GL4). GL where to work.
	 *  @param w (int). New width.
	 *  @param h (int). New height.
	 */
	@Override
	public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
		// Prevent a divide by zero, when window is too short
		// (you can't make a window of zero width).
		if (height == 0)
			height = 1;
 
		ratio = (1.0f * width) / height;
		this.projMatrix = Maths.buildProjectionMatrix(53.13f, ratio, 1.0f, 30.0f, this.projMatrix);
	}
 
	/** GL Render loop */
	@Override
	public void display(GLAutoDrawable drawable) {
		GL4 gl = drawable.getGL().getGL4();
		renderScene(gl);
	}
 
	/** GL Complete */
	@Override
	public void dispose(GLAutoDrawable drawable) {
		
	}
 

	
	/*
	void drawCircle(float x, float y, float z, float radius, int numberOfSides )  {
	    int numberOfVertices = numberOfSides + 2;
	    
	    float twicePi = 2.0f * (float) Math.PI;
	    
	    float[] circleVerticesX = new float[numberOfVertices];
	    float[] circleVerticesY = new float[numberOfVertices];
	    float[] circleVerticesZ = new float[numberOfVertices];
	    
	    circleVerticesX[0] = x;
	    circleVerticesY[0] = y;
	    circleVerticesZ[0] = z;
	    
	    for ( int i = 1; i < numberOfVertices; i++ )
	    {
	        circleVerticesX[i] = (float) (x + ( radius * Math.cos( i *  twicePi / numberOfSides ) ));
	        circleVerticesY[i] = (float) (y + ( radius * Math.sin( i * twicePi / numberOfSides ) ));
	        circleVerticesZ[i] = z;
	    }
	    
	    float[] allCircleVertices = new float[( numberOfVertices ) * 3];
	    
	    for ( int i = 0; i < numberOfVertices; i++ )
	    {
	        allCircleVertices[i * 3] = circleVerticesX[i];
	        allCircleVertices[( i * 3 ) + 1] = circleVerticesY[i];
	        allCircleVertices[( i * 3 ) + 2] = circleVerticesZ[i];
	    }
	    
	    //glEnableClientState( GL_VERTEX_ARRAY );
	    //glVertexPointer( 3, GL_FLOAT, 0, allCircleVertices );
	    //glDrawArrays( GL_TRIANGLE_FAN, 0, numberOfVertices);
	    //glDisableClientState( GL_VERTEX_ARRAY );
	}

	
	private int frameNumber = 0;
	
	private static boolean animating;
	
	private void updateFrame() {
		// TODO:  add any other updating required for the next frame.
		frameNumber++;
	}
*/

}