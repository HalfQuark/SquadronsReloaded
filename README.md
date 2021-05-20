# SquadronsReloaded
Movecraft addon which reimplements SquadronDirectors  
  
## Commands:  
/squadron  
 manoverboard - tps you to lead squadron craft  
 cruise [On,Off,North,East,South,West] - toggles squadron cruise  
 release [player] - releases own squadron or other player's squadron  
 scuttle [player] - scuttles own squadron or other player's squadron  
 formation  
 info [player] - displays own squadron info or other player's squadron info  
 list - lists all squadrons on the server  
  
## Squadron:  
 Form a squadron by left clicking craft signs (like Airskiff signs) while piloting a carrier type.  
 The carried types must be part of the carrier to be added to the squadron.  
 When any translation or rotation (including indirect and direct control cruising) is performed on a squadron craft  
all crafts of the squadron will try to mimic it.  
 The squadron pilot has to control the squadron from one of the crafts using the manoverboard command or sign.  
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

## Formations:  
  
## Custom signs:  
 SquadronRelease - same as release command  
 SquadronLead - same as manoverboard command  
  
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
  
## Config:  
 carriedTypes - list all craft types which can be carried aka be piloted as a squadron craft  
 carrierTypes - list all crafts which can be used to launch squadrons  
 pilotCheckTicks - specify how many ticks the plugin will wait after a squad craft is piloted to check if it's valid and add it to the squadron (you can leave it at 40)  
 manoverboardTime - specify the manoverboard timer when you fall from a squadron craft  
