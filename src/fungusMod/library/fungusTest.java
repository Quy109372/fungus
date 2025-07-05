package fungusMod.library;

import mindustry.Vars;
import mindustry.content.Blocks;
import mindustry.content.Fx;
import mindustry.content.Liquids;
import mindustry.gen.Building;
import mindustry.type.Liquid;
import mindustry.world.Tile;
import mindustry.world.blocks.environment.Floor;
import mindustry.world.blocks.storage.StorageBlock;
import arc.struct.Seq;
import arc.math.Mathf;

public class fungusTest {
    public static StorageBlock sporeCore;
    
    public static void load() {
        sporeCore = new StorageBlock("spore-core") {{
            health = 3000;
            armor = 1f;
            size = 3;
            
            // Liquid handling
            hasLiquids = true;
            outputsLiquid = true;
            itemCapacity = 4000;
            liquidCapacity = 1000f;
            
            // Correct liquid filter implementation
            liquidFilter = new boolean[Vars.content.liquids().size];
            liquidFilter[Liquids.water.id] = true;
            
            Seq<Floor> infectGround = Seq.with(
                (Floor)Blocks.moss,
                (Floor)Blocks.sporeMoss
            );

            buildType = () -> new StorageBuild() {
                @Override
                public void updateTile() {
                    super.updateTile();
                    
                    if (infectGround.contains(tile.floor())) {
                        if (Mathf.chanceDelta(0.09f)) {
                            int dx = Mathf.random(-1, 1);
                            int dy = Mathf.random(-1, 1);

                            if (dx == 0 && dy == 0) return;
                            
                            Tile target = tile.nearby(dx, dy);
                            if (target != null && target.block() == Blocks.air) {
                                target.setFloor((Floor)Blocks.sporeMoss);
                            }
                        }
                    }
                }

                @Override
                public boolean acceptLiquid(Building source, Liquid liquid) {
                    if (liquid != Liquids.water) {
                        if (source != null && !source.dead()) {
                            Fx.smoke.at(source.x, source.y);
                            source.kill();
                        }
                        return false;
                    }
                    return super.acceptLiquid(source, liquid);
                }
            };
        }};
    }
}