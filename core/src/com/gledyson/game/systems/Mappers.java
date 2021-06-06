package com.gledyson.game.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.gledyson.game.components.AnimationComponent;
import com.gledyson.game.components.Box2DBodyComponent;
import com.gledyson.game.components.CollisionComponent;
import com.gledyson.game.components.EnemyComponent;
import com.gledyson.game.components.LiquidFloorComponent;
import com.gledyson.game.components.PlayerComponent;
import com.gledyson.game.components.ProjectileComponent;
import com.gledyson.game.components.SpringComponent;
import com.gledyson.game.components.StateComponent;
import com.gledyson.game.components.TextureComponent;
import com.gledyson.game.components.TransformComponent;
import com.gledyson.game.components.TypeComponent;

public class Mappers {
    public static final ComponentMapper<AnimationComponent> animation = ComponentMapper.getFor(AnimationComponent.class);
    public static final ComponentMapper<CollisionComponent> collision = ComponentMapper.getFor(CollisionComponent.class);
    public static final ComponentMapper<PlayerComponent> player = ComponentMapper.getFor(PlayerComponent.class);
    public static final ComponentMapper<EnemyComponent> enemy = ComponentMapper.getFor(EnemyComponent.class);
    public static final ComponentMapper<LiquidFloorComponent> liquid = ComponentMapper.getFor(LiquidFloorComponent.class);
    public static final ComponentMapper<Box2DBodyComponent> body = ComponentMapper.getFor(Box2DBodyComponent.class);
    public static final ComponentMapper<TransformComponent> transform = ComponentMapper.getFor(TransformComponent.class);
    public static final ComponentMapper<TextureComponent> texture = ComponentMapper.getFor(TextureComponent.class);
    public static final ComponentMapper<TypeComponent> type = ComponentMapper.getFor(TypeComponent.class);
    public static final ComponentMapper<StateComponent> state = ComponentMapper.getFor(StateComponent.class);
    public static final ComponentMapper<ProjectileComponent> projectile = ComponentMapper.getFor(ProjectileComponent.class);
    public static final ComponentMapper<SpringComponent> spring = ComponentMapper.getFor(SpringComponent.class);
}
