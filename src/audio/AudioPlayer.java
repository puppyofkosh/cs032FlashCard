package audio;


public interface AudioPlayer {
	
	void play(AudioFile a); //Responsible for handling system sound output for the app.
	//Will automatically stop the playing one if there is one.
	void stop(); //Stops whatever AudioFile playback is currently in progress.
}
