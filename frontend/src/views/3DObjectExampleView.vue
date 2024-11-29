<template>
    <canvas ref="canvasRef"></canvas>
</template>
  
<script setup lang="ts">
  import { onMounted, ref } from 'vue'
  import * as THREE from "three"
  import { TransformControls } from 'three/addons/controls/TransformControls.js'
  import { OBJLoader } from 'three/addons/loaders/OBJLoader.js'
  import { GLTFLoader } from 'three/addons/loaders/GLTFLoader.js'
  import { MTLLoader } from 'three/addons/loaders/MTLLoader.js'
  
  const canvasRef = ref()
  let renderer: THREE.WebGLRenderer
  let controls: TransformControls
  const scene = new THREE.Scene()
  
  const camera = new THREE.PerspectiveCamera(45, window.innerWidth / window.innerHeight, 0.1, 100)
  camera.position.set(-20, 20, 20)
  camera.lookAt(0, 0, 0)
  scene.add(camera)
  
  const light = new THREE.DirectionalLight()
  light.position.set(0, 10, 10)
  light.castShadow = true
  scene.add(light)
  
  
  const objLoader = new OBJLoader()
  const mtlLoader = new MTLLoader()
  mtlLoader.load(
        'models/low-poly-mill.mtl',
        (materials) => {
            materials.preload()
            objLoader.setMaterials(materials)

            objLoader.load(
                'models/low-poly-mill.obj',
                function (object) {
                    scene.add(object)
                    object.position.set(0, 0, 0)
                    object.scale.set(0.1, 0.1, 0.1)
                    
                    console.log('Objekt geladen:', object)
                },
                function (xhr) {
                    console.log((xhr.loaded / xhr.total * 100) + '% geladen')
                },
                function (error) {
                    console.error('Fehler beim Laden der OBJ-Datei:', error)
                }
            )
        },
        (error) => {
            console.error('Fehler beim Laden MTL-Datei:', error)
        }
  )

  function objAnimate() {
    requestAnimationFrame(objAnimate)
    renderer.render(scene, camera)
 }

  let mixer: THREE.AnimationMixer
  const glbLoader = new GLTFLoader()
  
  glbLoader.load(
    '/models/sculptober_day_21_fresh.glb',
    (gltf) => {
        const model = gltf.scene
        model.position.set(10, 0, 10)
        model.scale.set(10, 10, 10)

        scene.add(model)

        mixer = new THREE.AnimationMixer(model)

        gltf.animations.forEach((clip) => {
            const action = mixer.clipAction(clip)
            action.play()
        })
        console.log('GLTF model loaded:', model)
    },
    (xhr) => {
        console.log((xhr.loaded / xhr.total) * 100 + '% loaded')
    },
    (error) => {
        console.error('Error loading GLB model:', error)
    }
  )

  
  function glbAnimate(delta: number) {
    if (mixer) {
        mixer.update(delta) // Update Mixer pro Frame
    }
    renderer.render(scene, camera)
    }

    // Call animate with delta time:
    let clock = new THREE.Clock()
    function renderLoop() {
        requestAnimationFrame(renderLoop)
        const delta = clock.getDelta() // Time between 2 Frame
        glbAnimate(delta)
    }
    
 
  
  
  onMounted(() => {
    renderer = new THREE.WebGLRenderer({
      canvas: canvasRef.value,
      alpha: true,
      antialias: true,
    })
    renderer.setSize(window.innerWidth, window.innerHeight)
    renderer.setPixelRatio(window.devicePixelRatio)
  
    renderer.shadowMap.enabled = true

    controls = new TransformControls(camera, renderer.domElement)

    objAnimate()
    renderLoop()

    renderer.render(scene, camera)
    window.addEventListener("resize", resizeCallback)
  })
  
  function resizeCallback(){
    renderer.setSize(window.innerWidth, window.innerHeight)
    camera.aspect = window.innerWidth/window.innerHeight
    camera.updateProjectionMatrix()
  }

</script>
  