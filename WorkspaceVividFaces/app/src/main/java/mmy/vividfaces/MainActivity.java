package mmy.vividfaces;

import android.content.DialogInterface;
import android.os.PowerManager;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.Toast;

import java.util.Iterator;
import java.util.Map;

import mmy.colladavividfaces.ModelCompatActivity;
import mmy.colladavividfaces.animStream.VFAnimation;
import mmy.colladavividfaces.animStream.VFAnimationGroup;
import mmy.colladavividfaces.model.Model;

public class MainActivity extends ModelCompatActivity {

    private PowerManager.WakeLock mWakelock;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.getIntent().putExtra("modelName", "dragon");
        super.onCreate(savedInstanceState);
        PowerManager pm = (PowerManager)getSystemService(POWER_SERVICE);// init powerManager
        mWakelock = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.SCREEN_DIM_WAKE_LOCK,"mmy.vividfaces:wake"); // this target for tell OS which app control screen
        mWakelock.acquire(); // Wake up Screen and keep screen lighting
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        Toast.makeText(getApplicationContext(), "First time loading is very slow and may take minutes.",
                Toast.LENGTH_SHORT).show();
    }

    String[] supportedModels = {
             "bear",
            "cat", "chicken",
            "dog", "dragon",
             "koala", "monkey", "pig", "rabbit", "tiger"
    };
    private static final int MENU_Pick_Model = Menu.FIRST;
    private static final int MENU_Pick_Animation = Menu.FIRST + 1;
    /* Creates the menu items */
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, MENU_Pick_Model, 0, "Model");
        menu.add(0, MENU_Pick_Animation, 0, "Animations");
        menu.getItem(0).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        menu.getItem(1).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        return true;
    }
    /* Handles item selections */
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case MENU_Pick_Model:
                pickModel();
                return true;
            case MENU_Pick_Animation:
                pickAnimations();
                return true;
        }
        return false;
    }
    private void pickModel(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Pick model");
        builder.setItems(supportedModels, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                pickModel(which);
            }
        });
        try {
            AlertDialog dialog = builder.create();
            dialog.show();
        }catch(Exception ee){
            Log.e("MyApp", "ee" + ee);
            ee.printStackTrace();;
        }
    }
    private void pickModel(int model){
        allAnims = null;
        pickedAnims = null;
        leftEyeMorph = null;
        rightEyeMorph = null;
        headMorph = null;
        lowerTeethMorph = null;
        whiskersMorph = null;
        tongueMorph = null;
        Toast.makeText(getApplicationContext(), "First time loading is very slow and may take minutes. Second time will be faster.",
                Toast.LENGTH_SHORT).show();
        super.pickModelByName(supportedModels[model]);
    }
    String[] allAnims = null;
    boolean[] pickedAnims = null;
    private void pickAnimations(){
        if(allAnims==null || allAnims.length==0){
            Toast.makeText(getApplicationContext(), "Wait a second for model loading.",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        new AlertDialog.Builder(this)
                    .setIcon(R.drawable.ic_launcher_background)
                    .setTitle("Pick Animations")
                    .setMultiChoiceItems(allAnims,
                            pickedAnims,
                            new DialogInterface.OnMultiChoiceClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton,
                                                    boolean isChecked) {
                                    if(isChecked){
                                        pickedAnims[whichButton] = true;
                                    }else{
                                        pickedAnims[whichButton] = false;
                                    }
                                }
                            })
                    .setPositiveButton("OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    putAnimationsToModel();
                                }
                            })
                    .setNegativeButton("Cancel",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                }
                            })
                    .create().show();
    }
    private void putAnimationsToModel(){
        getModel().removeAllFeatureAnimations();
        VFAnimationGroup animGroup = new VFAnimationGroup("pickedAnims", 10);
        animGroup.repeatCount = 50;
        int animsCount = getAllAnimsSize();
        for(int i=0; i<animsCount; i++){
            if(pickedAnims[i]){
                String itemText = allAnims[i];
                int animIndex = Integer.parseInt(itemText.split(" ")[0]);
                VFAnimation anim = new VFAnimation(getModel(), animIndex,10);
                anim.autoreverses = true;
                anim.repeatCount = 200;
                animGroup.addAnimation(anim);
            }
        }
        getModel().addFeatureAnimGroup(animGroup);
    }
    @Override
    public void onLoadProgress(int progress){
        Log.i("MyApp", "load " + progress);
    }
    Map<Integer, String> leftEyeMorph = null;
    Map<Integer, String> rightEyeMorph = null;
    Map<Integer, String> headMorph = null;
    Map<Integer, String> lowerTeethMorph = null;
    Map<Integer, String> whiskersMorph = null;
    Map<Integer, String> tongueMorph = null;
    private int getAllAnimsSize(){
        int ret = leftEyeMorph.size() + rightEyeMorph.size() + headMorph.size();
        if(lowerTeethMorph!=null){
            ret = ret + lowerTeethMorph.size();
        }
        if(whiskersMorph!=null){
            ret = ret + whiskersMorph.size();
        }
        if(tongueMorph!=null){
            ret = ret + tongueMorph.size();
        }
        return ret;
    }
    @Override
    public void onModelLoadSucess(){
        leftEyeMorph     = getModel().getLeftEye().getMorphTargets();
        rightEyeMorph    = getModel().getRightEye().getMorphTargets();
        headMorph        = getModel().getHead().getMorphTargets();
        if(getModel().getLowerTeeth()!=null) {
            lowerTeethMorph = getModel().getLowerTeeth().getMorphTargets();
        }
        if(getModel().getWhiskers()!=null) {
            whiskersMorph = getModel().getWhiskers().getMorphTargets();
        }
        if(getModel().getTongue()!=null) {
            tongueMorph = getModel().getTongue().getMorphTargets();
        }
        int allAnimSize = getAllAnimsSize();
        allAnims = new String[allAnimSize];
        pickedAnims = new boolean[allAnimSize];
        int index = 0;
        Iterator<Integer> iterator = leftEyeMorph.keySet().iterator();
        while(iterator.hasNext()){
            int animIndex = iterator.next();
            allAnims[index] = "" + animIndex + " L "+ leftEyeMorph.get(animIndex);
            index++;
        }
        iterator = rightEyeMorph.keySet().iterator();
        while(iterator.hasNext()){
            int animIndex = iterator.next();
            allAnims[index] = "" + animIndex + " R "+ rightEyeMorph.get(animIndex);
            index++;
        }
        iterator = headMorph.keySet().iterator();
        while(iterator.hasNext()){
            int animIndex = iterator.next();
            allAnims[index] = "" + animIndex + " H " + headMorph.get(animIndex);
            index++;
        }
        if(lowerTeethMorph!=null) {
            iterator = lowerTeethMorph.keySet().iterator();
            while (iterator.hasNext()) {
                int animIndex = iterator.next();
                allAnims[index] = "" + animIndex + " LT " + lowerTeethMorph.get(animIndex);
                index++;
            }
        }
        if(whiskersMorph!=null) {
            iterator = whiskersMorph.keySet().iterator();
            while (iterator.hasNext()) {
                int animIndex = iterator.next();
                allAnims[index] = "" + animIndex + " W " + whiskersMorph.get(animIndex);
                index++;
            }
        }
        if(tongueMorph!=null) {
            iterator = tongueMorph.keySet().iterator();
            while (iterator.hasNext()) {
                int animIndex = iterator.next();
                allAnims[index] = "" + animIndex + " T " + tongueMorph.get(animIndex);
                index++;
            }
        }
        Toast.makeText(getApplicationContext(), "Load finished.",
                Toast.LENGTH_SHORT).show();
    }
    @Override
    public void onModelLoadFail(){
        Toast.makeText(getApplicationContext(), "Load fail",
                Toast.LENGTH_SHORT).show();
    }
    @Override
    public void onStop(){
        super.onStop();
        if(mWakelock!=null){
            mWakelock.release();
        }
    }
}
