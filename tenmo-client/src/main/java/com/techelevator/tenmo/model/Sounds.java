package com.techelevator.tenmo.model;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

public class Sounds {
    public static void playSound(String soundFile) throws LineUnavailableException, IOException, UnsupportedAudioFileException {
        File f = new File("..\\module-2-capstone\\tenmo-client\\src\\main\\resources\\" + soundFile);//relative path
        AudioInputStream audioIn = AudioSystem.getAudioInputStream(f.toURI().toURL());//toURI properly formats any folder names with spaces instead of %20
        Clip clip = AudioSystem.getClip();
        clip.open(audioIn);
        clip.start();
    }
    //Sounds.playSound("happynoise.wav") <- syntax for sound activation command. file name changable to other noises in resources folder
}
