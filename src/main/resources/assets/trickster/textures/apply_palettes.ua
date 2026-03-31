# Experimental!

Fmt ← ⍜⊜□◌ ⊸⦷ "%s" ˜⊙∘
&rl□ &fo "../colors.txt"
$basePalette ◌°img&frab "palettes.png"
⊃↘↙1 # extract base palette
$palettes
&fo "templates.txt"
&rlbelow(
	on&s
  by(Fmt {""} unfix)fix
  $img ◌°img&frab
  ⤙⊙⊙◌
  ˜≡⊂[2_0_2_2] # add dummy value to palettes
  ⊙⤙⊙⊙◌
  ⊸≡⌟(˜⊏⊙⨂) # get color indices
  ⊙¤
  ≡₁(⊡⊙⊟⊸≍2_0_2_2) # replace dummy values with original colors
  ≡(back&fwa dipback(Fmt [id]) img "png")
	&s"done"
)
