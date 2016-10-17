package gamePackage.audio;

import javafx.scene.media.AudioClip;

import java.util.Random;

/**
 * Loads the necessary audio files at application startup.
 * Audio files can be played by calling AudioClip.play().
 * Volume and pan adjustment also available.
 *
 * Original author Matthew Sanchez
 *
 * @author Connor Denman & Ederin Igharoro
 */
public class AudioFiles
{
  public static AudioClip userStep1;
  public static AudioClip userStep2;
  public static AudioClip userAttack;
  public static AudioClip userSwing;
  public static AudioClip userHealth;
  public static AudioClip backgroundMusic;
  public static AudioClip[] zombieSounds = new AudioClip[24];
  public static Random random = new Random();

  static
  {
    userStep1 = new AudioClip(AudioFiles.class.getResource("/resources/playerAudioFiles/footstep1.wav").toString());
    userStep2 = new AudioClip(AudioFiles.class.getResource("/resources/playerAudioFiles/footstep2.wav").toString());
    userAttack = new AudioClip(AudioFiles.class.getResource("/resources/playerAudioFiles/punch.wav").toString());
    userSwing = new AudioClip(AudioFiles.class.getResource("/resources/playerAudioFiles/swing.wav").toString());
    userHealth = new AudioClip(AudioFiles.class.getResource("/resources/playerAudioFiles/swing.wav").toString());
    backgroundMusic = new AudioClip(AudioFiles.class.getResource("/resources/backgroundMusic.mp3").toString());
    backgroundMusic.setCycleCount(AudioClip.INDEFINITE);

    for (int i = 1; i <= 24; i++)
    {
      zombieSounds[i - 1] = new AudioClip(AudioFiles.class.getResource("/resources/zombieAudioFiles/zombie-" + i + ".wav").toString());
    }
  }

  /**
   * Retrieves a random zombie-related sound.
   *
   * @return AudioClip A random zombie-related sound
   */
  public static AudioClip randomZombieSound()
  {
    return zombieSounds[random.nextInt(zombieSounds.length)];
  }
}
