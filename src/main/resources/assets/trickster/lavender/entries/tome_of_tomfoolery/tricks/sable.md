```json
{
  "title": "Mechanical Analysis",
  "icon": "minecraft:compass",
  "category": "trickster:tricks",
  "ordinal": 100,
  "additional_search_terms": [
    "Aviator's Ingress",
    "Wayfinder's Ingress"
  ],
  "fabric:load_conditions": [
    {
      "condition": "fabric:any_mods_loaded",
      "values": [
        "sable"
      ]
    },
    {
      "condition": "fabric:not",
      "value": {
        "condition": "fabric:any_mods_loaded",
        "values": [
          "mage_mech"
        ]
      }
    }
  ]
}
```

In this world you may come across structures that have been severed from its foundational grid.


This section documents ways to get simple information about them. Any further applications would require further research.

;;;;;

The positions of blocks that make up Detached Structures, as they will be referred to as,
are represented by vectors of enormous magnitude.

These are useful if you wish to interact with said blocks, but they are often unfit for other operations.

;;;;;

<|trick@trickster:templates|trick-id=trickster:project_position|>

Converts a position on a Detached Structure to its current point in space.

;;;;;

<|trick@trickster:templates|trick-id=trickster:project_direction|>

Takes a position on a Detached Structure and rotates a vector by its orientation