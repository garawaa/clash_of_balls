package com.android.game.clash_of_the_balls.menu;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.opengl.GLES20;
import android.opengl.Matrix;
import android.support.v4.content.Loader.ForceLoadContentObserver;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.android.game.clash_of_the_balls.Font2D;
import com.android.game.clash_of_the_balls.GameSettings;
import com.android.game.clash_of_the_balls.R;
import com.android.game.clash_of_the_balls.Texture;
import com.android.game.clash_of_the_balls.TextureManager;
import com.android.game.clash_of_the_balls.VertexBufferFloat;
import com.android.game.clash_of_the_balls.game.RenderHelper;
import com.android.game.clash_of_the_balls.game.Vector;

/**
 * Menu item with keyboard input
 *
 */
public class MenuItemKeyboard extends MenuItem {

	
	private static final String LOG_TAG = "MenuItemKeyboard";
	
	private Context m_activity_context;
	private Texture m_texture;
	private TextureManager m_tex_manager;
	private String m_dialog_text;
	private String m_text_input="";
	
	private String m_new_str;
	
	private String m_regex = "^[a-zA-Z0-9]+$";
	private String m_regex_msg = "Please enter only letters or numbers";

	private Font2D m_item_font;
	
	public MenuItemKeyboard(Vector position, Vector size
			, Font2D.Font2DSettings font_settings, TextureManager tex_manager
			, Context activity_context, String text) {
		
		super(position, size);
		
		m_item_font = new Font2D(tex_manager, size, font_settings, (int)Math.round(size.y * 0.7));
		m_dialog_text = text;
		m_tex_manager=tex_manager;
		m_activity_context = activity_context;
		m_texture=m_tex_manager.get(R.raw.texture_grey_unpressed_button);
		m_position_data = new VertexBufferFloat
				(VertexBufferFloat.sprite_position_data, 3);
		m_color_data = new VertexBufferFloat
				(VertexBufferFloat.sprite_color_data_white, 4);
	}
	
	public String getString(){
		return m_text_input;
	}
	
	public void setString(String new_str) {
		m_text_input = new_str;
		m_item_font.setString(new_str);
	}
	
	private void setStringFromOutside(String new_str) {
		synchronized(this) {
			m_new_str = new String(new_str);
		}
	}
	
	public void onTouchDown(float x, float y) {
		// do nothing
	}
	public void onTouchUp(float x, float y) {
		
		
		((Activity) m_activity_context).runOnUiThread(new Runnable()
        {
            public void run()
            { 
            	AlertDialog.Builder alert = new AlertDialog.Builder(m_activity_context);

            	alert.setTitle("Clash of Balls");
            	alert.setMessage(m_dialog_text);

            	// Set an EditText view to get user input 
            	final EditText input = new EditText(m_activity_context);
            	input.setSingleLine();
            	input.addTextChangedListener(new TextValidator(input) {
            	    @Override
            	    public String validate(EditText textView, String text) {
            	    	Log.d(LOG_TAG,"validate: "+text);	    	
            	    	if(text.length()>0){
            	    	if(!text.matches(m_regex)){
            	    		text=text.substring(0,text.length()-1);
            	    		Toast.makeText(m_activity_context, m_regex_msg, Toast.LENGTH_SHORT).show();
            	    	}}
						return text;
            	    }
            	});
            	
            	alert.setView(input);

            	alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            	public void onClick(DialogInterface dialog, int whichButton) {
            		String str = input.getText().toString();
            		setStringFromOutside(str);
            		Log.d(LOG_TAG,"Text input: "+str);
            	  }
            	});

            	alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            	  public void onClick(DialogInterface dialog, int whichButton) {
            	    // Canceled.
            	  }
            	});

            	alert.show();
            }
            
            abstract class TextValidator implements TextWatcher {
                private EditText textView;

                public TextValidator(EditText textView) {
                    this.textView = textView;
                }

                public abstract String validate(EditText textView, String text);

                final public void afterTextChanged(Editable s) {
                    String text = textView.getText().toString();
                    String newString = validate(textView, text);
                    textView.setSelection(text.length());
                    if(!newString.equals(text))textView.setText(newString);
                   
                }

                final public void beforeTextChanged(CharSequence s, int start, int count, int after) { /* Don't care */ }

                final public void onTextChanged(CharSequence s, int start, int before, int count) { /* Don't care */ }
            }
            
         }
  );

	}
	
	public void move(float dsec) {
		synchronized (this) {
			if(m_new_str != null) {
				setString(m_new_str);
				m_new_str = null;
			}
		}
	}

	public void draw(RenderHelper renderer) {			
		
		int model_mat_pos = renderer.pushModelMat();
		float model_mat[] = renderer.modelMat();
		Matrix.translateM(model_mat, model_mat_pos, m_position.x, m_position.y, 0.f);
		Matrix.scaleM(model_mat, model_mat_pos, this.size().x, this.size().y, 0.f);
		
		drawTexture(renderer, m_texture);
		
		m_item_font.draw(renderer);
        
        renderer.popModelMat();
	}
	
}
