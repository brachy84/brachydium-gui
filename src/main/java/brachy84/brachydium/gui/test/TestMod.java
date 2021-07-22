package brachy84.brachydium.gui.test;

import brachy84.brachydium.gui.UiFactoryRegistry;
import brachy84.brachydium.gui.internal.BlockEntityUiFactory;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class TestMod implements ModInitializer {

    public static final TestBlock BLOCK = new TestBlock();

    @Override
    public void onInitialize() {
        Identifier TEST_BLOCK_ID = new Identifier("testmod", "testblock");
        Registry.register(Registry.BLOCK, TEST_BLOCK_ID, BLOCK);
        Registry.register(Registry.ITEM, TEST_BLOCK_ID, new BlockItem(BLOCK, new FabricItemSettings().group(ItemGroup.REDSTONE)));
        TestBlockEntity.TYPE = Registry.register(Registry.BLOCK_ENTITY_TYPE, TEST_BLOCK_ID, FabricBlockEntityTypeBuilder.create(TestBlockEntity::new, BLOCK).build(null));
    }
}
