# SquadronsReloaded
Movecraft addon which reimplements SquadronDirectors.  
Code from Movecraft https://github.com/APDevTeam/Movecraft was used.  
Use legacy branch for Movecraft 7 and 1.12 spigot.
  
## Commands:  
/squadron  
 lead - tps you to lead squadron craft  
 carrier - tps you to the carrier craft  
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
 movecraft.squadron.lead - permission to use the lead command  
 movecraft.squadron.carrier - permission to use the lead command  
 movecraft.squadron.cruise - permission to use the cruise command  
 movecraft.squadron.release - permission to use the release command  
 movecraft.squadron.release.others - permission to release other squadrons  
 movecraft.squadron.scuttle - permission to use the scuttle command  
 movecraft.squadron.scuttle.others - permission to scuttle other squadrons    
 movecraft.squadron.info - permission to use the info command  
 movecraft.squadron.info.others - permission to view other squadrons' info  
 movecraft.squadron.list - permission to use the list command  
 movecraft.squadron.pilot - permission to pilot squadrons  
 movecraft.squadron.formation - permission to use the formations command  
  
## Config:  
 carriedTypes - list all craft types which can be carried aka be piloted as a squadron craft  
 carrierTypes - list all crafts which can be used to launch squadrons  
 needsCarrier - if squadron crafts need to be part of a carrier to be piltoed    
 manoverboardTime - specify the manoverboard timer when you fall from a squadron craft  
 tpToNewLead - if true the plugin will try to tp the player to the new squadron lead craft when the current one is sunk if the player is standing on it  
 squadMaxSize - (double) specify the base maximum number of crafts allowed on a squadron  
 squadMaxSizeCarrierMultiplier - (double) specify the carrier displacement multiplier  
 ```
  Final squadron max number of crafts = squadMaxSize + squadMaxSizeCarrierMultiplier * carrier displacement when piloted  
 ```
 squadMaxDisplacement - (double) specify the base maximum displacement allowed on a squadron  
 squadMaxDisplacementCarrierMultiplier: - (double) specify the carrier displacement multiplier  
 ```
  Final squadron max displacement = squadMaxDisplacement + squadMaxDisplacementCarrierMultiplier * carrier displacement when piloted  
 ```
 turnTicks - specify how many ticks the plugin will wait to try to correct a craft's orientation after it moves  
 formationRoundDistance - specify min distance from formation position at which crafts won't attempt to form up anymore  
 formationSpeedMultiplier - speed multiplied used when forming up, multiplied to the default movement tick delay of the crafts  
 syncedSigns - list of types of sign which will be syncronised across squadron crafts  

