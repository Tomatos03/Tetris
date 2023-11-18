package GameCore;

public class BlockV4 {
	static final boolean[][][] Shape = {
			// I型方块
			{
				{ false, false, false, false },
				{ true, true, true, true },
				{ false, false, false, false },
				{ false, false, false, false }
			},
			// J型方块
			{ 
				{ true, false, false },
				{ true, true, true }, 
				{ false, false, false } 
			},
			// L型方块
			{ 
				{ false, false, true }, 
				{ true, true, true }, 
				{ false, false, false }
			},
			// O型方块
			{ 
				{ true, true }, 
				{ true, true } 
			},
			// S型方块
			{ 
				{ false, true, true }, 
				{ true, true, false }, 
				{ false, false, false } 
			},
			// T型方块
			{ 
				{ false, true, false }, 
				{ true, true, true }, 
				{ false, false, false } 
			},
			// Z型方块
			{ 
				{ true, true, false }, 
				{ false, true, true }, 
				{ false, false, false } 
			}
	};
}
