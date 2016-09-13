package gamePackage.audio;

import javafx.scene.media.AudioClip;

import java.util.Random;

/**
 * Loads the necessary audio files at application startup.
 * Audio files can be played by calling AudioClip.play().
 * Volume and pan adjustment also available.
 *
 * @author Maxwell Sanchez
 */
public class AudioFiles
{
  public static AudioClip userStep1 = null;
  public static AudioClip userStep2 = null;
  public static AudioClip[] zombieSounds = new AudioClip[24];
  public static Random random = new Random();

  static
  {
    userStep1 = new AudioClip(AudioFiles.class.getResource("/resources/footstep1.wav").toString());
    userStep2 = new AudioClip(AudioFiles.class.getResource("/resources/footstep2.wav").toString());
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
