// Importiere THREE
import * as THREE from 'three';
import { type IGameMap, MapObjectType } from "@/stores/IGameMapDTD";
import { SnackType } from "@/stores/Snack/ISnackDTD";
import { useGameMapStore } from "@/stores/gameMapStore";

// Importiere Texturen
import wallColorTexturePath from '@/assets/Bricks066_1K-JPG_Color.jpg';
import wallNormalTexturePath from '@/assets/Bricks066_1K-JPG_NormalDX.jpg';
import wallAOTexturePath from '@/assets/Bricks066_1K-JPG_AmbientOcclusion.jpg';
import wallRoughnessTexturePath from '@/assets/Bricks066_1K-JPG_Roughness.jpg';
import floorColorTexturePath from '@/assets/Grass001_1K-JPG_Color.jpg';
import floorNormalTexturePath from '@/assets/Grass001_1K-JPG_NormalDX.jpg';
import floorAOTexturePath from '@/assets/Grass001_1K-JPG_AmbientOcclusion.jpg';
import floorRoughnessTexturePath from '@/assets/Grass001_1K-JPG_Roughness.jpg';

// Importiere Skybox-Bilder
import bkImage from '@/assets/skybox_bk.png';
import dnImage from '@/assets/skybox_dn.png';
import ftImage from '@/assets/skybox_ft.png';
import lfImage from '@/assets/skybox_lf.png';
import rtImage from '@/assets/skybox_rt.png';
import upImage from '@/assets/skybox_up.png';

/**
 * for rendering the game map
 */
export const GameMapRenderer = () => {
  const gameMapStore = useGameMapStore();
  const scene = gameMapStore.getScene();

  // Scene Setup
  const GROUNDSIZE = 1000;
  let renderer: THREE.WebGLRenderer;

  // Texturen laden mit Debugging
  const textureLoader = new THREE.TextureLoader();

  // Lade die Farb-Textur
  const wallColorTexture = textureLoader.load(
    wallColorTexturePath,
    () => console.log('Color texture loaded successfully'),
    undefined,
    (error) => console.error('Error loading color texture at path', wallColorTexturePath, error)
  );

  // Lade die Normal-Map
  const wallNormalTexture = textureLoader.load(
    wallNormalTexturePath,
    () => console.log('Normal texture loaded successfully'),
    undefined,
    (error) => console.error('Error loading normal texture at path', wallNormalTexturePath, error)
  );

  // Lade die Ambient Occlusion Map
  const wallAOTexture = textureLoader.load(
    wallAOTexturePath,
    () => console.log('Ambient Occlusion texture loaded successfully'),
    undefined,
    (error) => console.error('Error loading AO texture at path', wallAOTexturePath, error)
  );

  // Lade die Roughness Map
  const wallRoughnessTexture = textureLoader.load(
    wallRoughnessTexturePath,
    () => console.log('Roughness texture loaded successfully'),
    undefined,
    (error) => console.error('Error loading roughness texture at path', wallRoughnessTexturePath, error)
  );

  // Lade die Boden-Texturen
  const floorColorTexture = textureLoader.load(
    floorColorTexturePath,
    () => console.log('Floor color texture loaded successfully'),
    undefined,
    (error) => console.error('Error loading floor color texture at path', floorColorTexturePath, error)
  );

  const floorNormalTexture = textureLoader.load(
    floorNormalTexturePath,
    () => console.log('Floor normal texture loaded successfully'),
    undefined,
    (error) => console.error('Error loading floor normal texture at path', floorNormalTexturePath, error)
  );

  const floorAOTexture = textureLoader.load(
    floorAOTexturePath,
    () => console.log('Floor AO texture loaded successfully'),
    undefined,
    (error) => console.error('Error loading floor AO texture at path', floorAOTexturePath, error)
  );

  const floorRoughnessTexture = textureLoader.load(
    floorRoughnessTexturePath,
    () => console.log('Floor roughness texture loaded successfully'),
    undefined,
    (error) => console.error('Error loading floor roughness texture at path', floorRoughnessTexturePath, error)
  );

  // Beleuchtung
  const light = new THREE.DirectionalLight(0xffffff, 1);
  light.position.set(5, 10, 10);
  light.castShadow = true;
  scene.add(light);

  const hemiLight = new THREE.HemisphereLight(0xffffbb, 0x080820, 1);
  scene.add(hemiLight);

  

  
  //SKYBOX
  let skyboxCube: THREE.Mesh | null = null;

  const addSkybox = () => {
    const geometry = new THREE.BoxGeometry(100, 100, 100);
    const cubeMaterials = [
      new THREE.MeshBasicMaterial({ map: textureLoader.load(ftImage), side: THREE.DoubleSide }), // Rückseite
      new THREE.MeshBasicMaterial({ map: textureLoader.load(bkImage), side: THREE.DoubleSide }), // Boden
      new THREE.MeshBasicMaterial({ map: textureLoader.load(upImage), side: THREE.DoubleSide }), // Vorderseite
      new THREE.MeshBasicMaterial({ map: textureLoader.load(dnImage), side: THREE.DoubleSide }), // Links
      new THREE.MeshBasicMaterial({ map: textureLoader.load(rtImage), side: THREE.DoubleSide }), // Rechts
      new THREE.MeshBasicMaterial({ map: textureLoader.load(lfImage), side: THREE.DoubleSide }), // Oben
    ];

    skyboxCube = new THREE.Mesh(geometry, cubeMaterials);
    skyboxCube.position.set(15,5,15);
    scene.add(skyboxCube);
  };

  const getSkyboxCube = () => skyboxCube;
  

  /**
   * Wall erstellen
   */
  const createWall = (
    xPosition: number,
    yPosition: number,
    zPosition: number,
    height: number,
    sideLength: number,
  ) => {
    // Prüfen, ob die Texturen geladen wurden
    console.log('Using textures:', { wallColorTexture, wallNormalTexture, wallAOTexture, wallRoughnessTexture });

    const wallMaterial = new THREE.MeshStandardMaterial({
      map: wallColorTexture,             // Basisfarbe
      normalMap: wallNormalTexture,      // Normalenkarte
      aoMap: wallAOTexture,              // Ambient Occlusion Map
      roughnessMap: wallRoughnessTexture, // Rauhigkeitsmap
      metalness: 0.0,                    // Kein Metall-Effekt
      emissive: new THREE.Color(0x000000), // Emissivität auf Schwarz setzen
    });

    // Wende eine Texturkoordinaten-Übereinstimmung an
    wallMaterial.aoMap = wallAOTexture;
    wallMaterial.roughnessMap = wallRoughnessTexture;
    wallMaterial.needsUpdate = true;

    const wallGeometry = new THREE.BoxGeometry(sideLength, height, sideLength);
    const wall = new THREE.Mesh(wallGeometry, wallMaterial);

    wall.position.set(xPosition, yPosition, zPosition);
    wall.castShadow = true;
    wall.receiveShadow = true;
    scene.add(wall);
  };

  // Renderer initialisieren
  const initRenderer = (canvas: HTMLCanvasElement): THREE.WebGLRenderer => {
    renderer = new THREE.WebGLRenderer({
      canvas,
      alpha: true,
      antialias: true, // aktiviert Kantenglättung für bessere Bildqualität
    });
    renderer.setSize(window.innerWidth, window.innerHeight);
    renderer.setPixelRatio(window.devicePixelRatio);
    renderer.shadowMap.enabled = true; // aktiviert die Schattenberechnung

    // Setze die Hintergrundfarbe auf transparent
    //renderer.setClearColor(0x000000, 0); // 0x000000 bedeutet schwarz, der zweite Wert '0' für volle Transparenz

    addSkybox();

    return renderer;
  };

  // Methode, um die Szene zurückzugeben
  const getScene = () => {
    return scene;
  };

  // Methode, um die Spielfläche zu erstellen
  const createGameMap = (mapData: IGameMap) => {
    const OFFSET = mapData.DEFAULT_SQUARE_SIDE_LENGTH / 2;
    const DEFAULT_SIDE_LENGTH = mapData.DEFAULT_SQUARE_SIDE_LENGTH;
    const WALL_HEIGHT = mapData.DEFAULT_WALL_HEIGHT;

    createGround();

    // Iteriere durch die Map-Daten und erstelle Wände
    for (const [id, square] of mapData.gameMap) {
      if (square.type === MapObjectType.WALL) {
        createWall(
          square.indexX * DEFAULT_SIDE_LENGTH + OFFSET,
          0,
          square.indexZ * DEFAULT_SIDE_LENGTH + OFFSET,
          WALL_HEIGHT,
          DEFAULT_SIDE_LENGTH
        );
      }
      if (square.type === MapObjectType.FLOOR) {
        const squareToAdd = createFloorSquare(
          square.indexX * DEFAULT_SIDE_LENGTH + OFFSET,
          square.indexZ * DEFAULT_SIDE_LENGTH + OFFSET,
          DEFAULT_SIDE_LENGTH
        );
        scene.add(squareToAdd);

        if (square.snack != null) {
          const snackToAdd = createSnackOnFloor(
            square.indexX * DEFAULT_SIDE_LENGTH + OFFSET,
            square.indexZ * DEFAULT_SIDE_LENGTH + OFFSET,
            DEFAULT_SIDE_LENGTH,
            square.snack?.snackType
          );
          scene.add(snackToAdd);
          gameMapStore.setSnackMeshId(id, snackToAdd.id);
        }
      }
    }
  };

  // Methode zur Erstellung eines Snacks auf dem Boden
  const createSnackOnFloor = (
    xPosition: number,
    zPosition: number,
    sideLength: number,
    type: SnackType
  ) => {
    let color = 'blue';

    if (type == SnackType.STRAWBERRY) {
      color = 'purple';
    } else if (type == SnackType.ORANGE) {
      color = 'orange';
    } else if (type == SnackType.CHERRY) {
      color = 'red';
    } else if (type == SnackType.APPLE) {
      color = 'green';
    }

    const SNACK_WIDTH_AND_DEPTH = sideLength / 3;
    const SNACK_HEIGHT = 1;
    const snackMaterial = new THREE.MeshStandardMaterial({ color: color });
    const snackGeometry = new THREE.BoxGeometry(SNACK_WIDTH_AND_DEPTH, SNACK_HEIGHT, SNACK_WIDTH_AND_DEPTH);
    const snack = new THREE.Mesh(snackGeometry, snackMaterial);

    snack.position.set(xPosition, 0, zPosition);

    return snack;
  };

  // Methode zur Erstellung eines Bodens
  const createFloorSquare = (
    xPosition: number,
    zPosition: number,
    sideLength: number,
  ) => {
    const squareHeight = 0.1; // Höhe des Bodens
    // Erstelle das Material mit den Boden-Texturen
    const squareMaterial = new THREE.MeshStandardMaterial({
      map: floorColorTexture,         // Farb-Textur
      normalMap: floorNormalTexture,  // Normalenkarte
      aoMap: floorAOTexture,          // Ambient Occlusion Map
      roughnessMap: floorRoughnessTexture, // Rauhigkeitskarte
    });
    const squareGeometry = new THREE.BoxGeometry(sideLength, squareHeight, sideLength);
    const square = new THREE.Mesh(squareGeometry, squareMaterial);

    square.position.set(xPosition, 0, zPosition);

    return square;
  };

  // Methode zur Erstellung des Bodens
  const createGround = () => {
    const groundGeometry = new THREE.PlaneGeometry(GROUNDSIZE, GROUNDSIZE);
    const groundMaterial = new THREE.MeshMatcapMaterial({ color: 'lightgrey' });
    const ground = new THREE.Mesh(groundGeometry, groundMaterial);

    ground.castShadow = true;
    ground.receiveShadow = true;
    ground.rotateX(-Math.PI / 2);
    ground.position.set(0, 0, 0);

    scene.add(ground);
  };

  return { initRenderer, createGameMap, getScene, getSkyboxCube };
};


