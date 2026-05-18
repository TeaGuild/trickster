```json
{
  "title": "The Drifter",
  "icon": "minecraft:ender_pearl",
  "category": "trickster:ploys",
  "additional_search_terms": [
    "Ploy of the Entitled",
    "Ploy of the Usurper"
  ]
}
```

awawa

;;;;;

<|ploy@trickster:templates|trick-id=trickster:prepare_displace,cost=120G + 1.35G^offset|>

Creates a temporary displacement fragment from an entity.
These fragments can only be used for five seconds after creation.

;;;;;

<|ploy@trickster:templates|trick-id=trickster:displace,cost=20G + 1.35G^offset + distance * 3G|>

Consumes a displacement fragment to teleport its entity to the given position relative to the caster.
If the entity is not the caster, a delay is applied.

;;;;;

Distance for displacement is calculated from the subjects current position to the casters,
and an additional cost of 1000 Gandalfs will be added if they are in different dimensions.


Displacement fragments can only be used once before becoming invalid.

;;;;;

<|trick@trickster:templates|trick-id=trickster:get_position|>

Given a displacement fragment, returns the position of its entity.

;;;;;

<|trick@trickster:templates|trick-id=trickster:get_dimension|>

Returns the dimension that a displacement fragment came from.

