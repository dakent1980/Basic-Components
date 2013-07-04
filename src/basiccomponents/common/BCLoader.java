package basiccomponents.common;

import java.util.Arrays;
import universalelectricity.core.UniversalElectricity;
import universalelectricity.prefab.ConductorChunkInitiate;
import universalelectricity.prefab.network.PacketManager;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Init;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.ModMetadata;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;

@Mod(modid = BCLoader.ID, name = BasicComponents.NAME, version = UniversalElectricity.VERSION, useMetadata = true)
@NetworkMod(channels = BCLoader.CHANNEL, clientSideRequired = true, serverSideRequired = false, packetHandler = PacketManager.class)
public class BCLoader
{
	public static final String ID = "BasicComponents";
	public static final String CHANNEL = "BasicComp";

	@Instance(ID)
	public static BCLoader instance;

	@Mod.Metadata(ID)
	public static ModMetadata metadata;

	@EventHandler
	public void preInit(FMLPreInitializationEvent event)
	{
		BasicComponents.CHANNEL = CHANNEL;
		BasicComponents.requestAll(this);

		ConductorChunkInitiate.register();
	}

    @EventHandler
	public void load(FMLInitializationEvent evt)
	{
		metadata.modId = BCLoader.CHANNEL;
		metadata.name = BasicComponents.NAME;
		metadata.description = "Universal Electricity is a modular coding framework that " + "allows the use of electricity in Minecraft. Mods which uses the Universal " + "Electricity API have the ability to communicate and be compatible with each other. ";

		metadata.url = "http://www.universalelectricity.com/";

		metadata.logoFile = "/universal_electricity.png";
		metadata.version = UniversalElectricity.VERSION + "." + UniversalElectricity.BUILD_VERSION;
		metadata.authorList = Arrays.asList(new String[] { "Calclavia" });
		metadata.credits = "Please visit the website.";
		metadata.autogenerated = false;
	}
}
