# SquadronsReloaded
Movecraft addon which reimplements SquadronDirectors.  
Code from Movecraft https://github.com/APDevTeam/Movecraft was used.
  
## Commands:  
/squadron  
 manoverboard - tps you to lead squadron craft  
 cruise [On,Off,North,East,South,West] - toggles squadron cruise  
 release [player] - releases own squadron or other player's squadron  
 scuttle [player] - scuttles own squadron or other player's squadron  
 formation [ON/OFF] [formation] [spacing] - turns formation on or off  
 info [player] - displays own squadron info or other player's squadron info  
 list - lists all squadrons on the server  
  
## Squadron:  
 Form a squadron by left clicking craft signs (like Airskiff signs) while piloting a carrier type.  
 The carried types must be part of the carrier to be added to the squadron.  
 When any translation or rotation (including indirect and direct control cruising) is performed on a squadron craft  
all crafts of the squadron will try to mimic it.  
 Each craft is attributed an id when added to the squadron, starting at 0 (Changed from 1 to 0 due to formation expressions) and increasing.  
 The squadron pilot has to control the squadron from one of the crafts using the manoverboard command or sign.  
 The manoverboard/sign will take the pilot to the lead craft which is the craft with the minimum id in the squadron at that point.  
 The pilot will not get released from the carrier while they are on board any squadron's craft.  
  
## Signs on crafts:  
 ### These work  
  AscendSign  
  ContactsSign  
  CraftSign  
  CruiseSign  
  DescendSign  
  HelmSign  
  SpeedSign  
  StatusSign  
  SubcraftRotateSign  
 ### These should not work  
  MoveSign  
  NameSign  
  PilotSign  
  RelativeMoveSign  
  ReleaseSign  
  RemoteSign  
  TeleportSign  
  
## Redstone components:  
 Buttons, levers, repeaters and comparators are synchronised across all squadron crafts.  
 They update when changed.  
 Buttons will not get stuck on Pressed state.  
 They need to be color coded, for example:  
  - When a lever on a red wool is flicked all levers on red wool are flicked to the same state.  
  - When a repeater on top of a white wool is adjusted all repeaters on white wool are adjusted to the same ticks.  
  - When a comparator on top of a double stone slab is toggled all comparators on double stone slab are toggled to the same subtraction mode state.  
  
## Synced signs:  
 When right-clicking a synced sign on a squadron craft, all signs of the same type (for example: LaunchTorpedo) sharing any line(string) with the clicked sign will be also activated.  
 The first line of the clicked sign won't be considered, so if for example we want to fire all torpedoes either include the same name in all LaunchTorpedo signs or include another line which also says LaunchTorpedo on the clicked sign.  
## Formations:  
 When forming up a formation name and spacing must be specified.  
 Crafts will always try to form up when not cruising.  
 Formations can be customised via .formation files in the Formations folder:  
    name: [Formation Name]  
    minSpacing: Minimum valid integer for spacing  
    maxSpacing: Maximum valid integer for spacing  
    positionMap:  
      SOUTH:  
        x: [Expression]  
        y: [Expression]  
        z: [Expression]  
      NORTH:  
        x: [Expression]  
        y: [Expression]  
        z: [Expression]  
      WEST:  
        x: [Expression]  
        y: [Expression]  
        z: [Expression]  
      EAST:  
        x: [Expression]  
        y: [Expression]  
        z: [Expression]  
 Expressions are used to determine each craft's relative position to the lead craft. 
 They can use the opperators: + - * / and ^  
 They can use 2 variables:  
    n : craft's rank, ammount of crafts in the squadron with id smaller than the craft's id (integer)  
    s : spacing specified on the formation command/sign  
 The relative position of a craft is then calculated by the x, y and z expressions of the cardinal direction of the squadron's movement.  
 If no direction of movement was detected, it will default to NORTH.  
  
## Direction correction:  
 On detection if conflicting cruise signs are found the craft won't be added to the squadron.  
 The "front" of a craft is determined by the cruise sign on detection.  
 If this is missing it is determined by the first cruise movement it performs.  
 Crafts will always try to correct their orientation to face in the same direction as the lead craft.  

## Custom signs:  
 SquadronRelease - same as release command  
 SquadronLead - same as manoverboard command  
 Formation  
 Line 2:[Formation name]  
 Line 3:[Spacing] - right click to form up, left click to turn off formation  
  
## Permissions:  
  movecraft.squadron.manoverboard  
  movecraft.squadron.cruise  
  movecraft.squadron.release  
 movecraft.squadron.release.others  
 movecraft.squadron.scuttle  
 movecraft.squadron.scuttle.others  
 movecraft.squadron.info  
 movecraft.squadron.info.others  
 movecraft.squadron.list  
 movecraft.squadron.pilot  
 movecraft.squadron.formation  
  
## Config:  
 carriedTypes - list all craft types which can be carried aka be piloted as a squadron craft  
 carrierTypes - list all crafts which can be used to launch squadrons  
 pilotCheckTicks - specify how many ticks the plugin will wait after a squad craft is piloted to check if it's valid and add it to the squadron (you can leave it at 40)  
 manoverboardTime - specify the manoverboard timer when you fall from a squadron craft  
 squadMaxSize - (double) specify the base maximum number of crafts allowed on a squadron  
 squadMaxSizeCarrierMultiplier - (double) specify the carrier displacement multiplier  
   Final squadron max number of crafts = squadMaxSize + squadMaxSizeCarrierMultiplier * carrier displacement when piloted  
 squadMaxDisplacement - (double) specify the base maximum displacement allowed on a squadron  
 squadMaxDisplacementCarrierMultiplier: - (double) specify the carrier displacement multiplier  
   Final squadron max displacement = squadMaxDisplacement + squadMaxDisplacementCarrierMultiplier * carrier displacement when piloted  
 turnTicks - specify how many ticks the plugin will wait to try to correct a craft's orientation after it moves  
 formationRoundDistance - specify min distance from formation position at which crafts won't attempt to form up anymore  
 formationSpeedMultiplier - speed multiplied used when forming up, multiplied to the default movement tick delay of the crafts  
 syncedSigns - list of types of sign which will be syncronised across squadron crafts  

