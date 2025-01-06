import * as THREE from 'three'

class SoundManager {
  private listener: THREE.AudioListener
  private sounds: Map<string, THREE.Audio | THREE.PositionalAudio>

  constructor() {
    this.listener = new THREE.AudioListener()
    this.sounds = new Map()
  }

  initListener(camera: THREE.Camera) {
    camera.add(this.listener)
  }

  loadSound(
    name: string,
    url: string,
    isPositional: boolean = false,
    options: any = {},
  ) {
    const audioLoader = new THREE.AudioLoader()
    audioLoader.load(
      url,
      buffer => {
        console.log(`Sound loaded: ${name}`)

        let sound

        if (isPositional) {
          sound = new THREE.PositionalAudio(this.listener)

          sound.setRefDistance(options.refDistance || 20)
          sound.setMaxDistance(options.maxDistance || 100)
          sound.setRolloffFactor(options.rolloffFactor || 1)
          sound.setDistanceModel(options.distanceModel || 'linear')
        } else {
          sound = new THREE.Audio(this.listener)
        }

        sound.setBuffer(buffer)
        sound.setLoop(options.loop || false)
        sound.setVolume(options.volume || 1)

        this.sounds.set(name, sound)
      },
      undefined,
      err => {
        console.error(`Error loading sound: ${name}`, err)
      },
    )
  }

  playSound(name: string) {
    const sound = this.sounds.get(name)
    if (sound) {
      sound.play()
    }
  }

  stopSound(name: string) {
    const sound = this.sounds.get(name)
    if (sound) {
      sound.stop()
    }
  }

  getSound(name: string) {
    return this.sounds.get(name)
  }
}

export const soundManager = new SoundManager()
