/*******************************************************************************
 * The MIT License
 *
 * Copyright (c) 2019 knokko
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *  
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *******************************************************************************/
package nl.knokko.customitems.editor.menu.edit.item;

import nl.knokko.customitems.editor.menu.edit.EditMenu;
import nl.knokko.customitems.editor.menu.edit.EditProps;
import nl.knokko.customitems.editor.set.item.CustomBow;
import nl.knokko.customitems.editor.set.item.NamedImage;
import nl.knokko.customitems.editor.set.item.texture.BowTextures;
import nl.knokko.customitems.item.AttributeModifier;
import nl.knokko.customitems.item.AttributeModifier.Attribute;
import nl.knokko.customitems.item.AttributeModifier.Operation;
import nl.knokko.customitems.item.AttributeModifier.Slot;
import nl.knokko.customitems.item.CustomItemType.Category;
import nl.knokko.gui.component.image.CheckboxComponent;
import nl.knokko.gui.component.text.FloatEditField;
import nl.knokko.gui.component.text.IntEditField;
import nl.knokko.gui.component.text.TextComponent;
import nl.knokko.gui.util.Option;

public class EditItemBow extends EditItemTool {
	
	private static final AttributeModifier EXAMPLE_ATTRIBUTE_MODIFIER = new AttributeModifier(Attribute.MOVEMENT_SPEED, Slot.OFFHAND, Operation.MULTIPLY, 1.5);

	private final CustomBow previous;

	private final FloatEditField damageMultiplier;
	private final FloatEditField speedMultiplier;
	private final IntEditField knockbackStrength;
	private final CheckboxComponent gravity;
	
	private final IntEditField shootDurabilityLoss;

	public EditItemBow(EditMenu menu, CustomBow previous) {
		super(menu, previous, Category.BOW);
		this.previous = previous;
		if (previous != null) {
			damageMultiplier = new FloatEditField(previous.getDamageMultiplier(), 0, EditProps.EDIT_BASE,
					EditProps.EDIT_ACTIVE);
			speedMultiplier = new FloatEditField(previous.getSpeedMultiplier(), 0, EditProps.EDIT_BASE,
					EditProps.EDIT_ACTIVE);
			knockbackStrength = new IntEditField(previous.getKnockbackStrength(), 0, EditProps.EDIT_BASE,
					EditProps.EDIT_ACTIVE);
			shootDurabilityLoss = new IntEditField(previous.getShootDurabilityLoss(), 0, EditProps.EDIT_BASE,
					EditProps.EDIT_ACTIVE);
			gravity = new CheckboxComponent(previous.hasGravity());
		} else {
			damageMultiplier = new FloatEditField(1, 0, EditProps.EDIT_BASE, EditProps.EDIT_ACTIVE);
			speedMultiplier = new FloatEditField(1, 0, EditProps.EDIT_BASE, EditProps.EDIT_ACTIVE);
			knockbackStrength = new IntEditField(0, 0, EditProps.EDIT_BASE, EditProps.EDIT_ACTIVE);
			shootDurabilityLoss = new IntEditField(1, 0, EditProps.EDIT_BASE, EditProps.EDIT_ACTIVE);
			gravity = new CheckboxComponent(true);
		}
	}
	
	@Override
	protected AttributeModifier getExampleAttributeModifier() {
		return EXAMPLE_ATTRIBUTE_MODIFIER;
	}

	@Override
	protected void addComponents() {
		super.addComponents();
		addComponent(new TextComponent("Durability loss on shooting:", EditProps.LABEL), 0.6f, 0.35f, 0.84f, 0.425f);
		addComponent(shootDurabilityLoss, 0.85f, 0.35f, 0.9f, 0.425f);
		addComponent(new TextComponent("Damage multiplier: ", EditProps.LABEL), 0.71f, 0.245f, 0.895f, 0.32f);
		addComponent(damageMultiplier, 0.895f, 0.245f, 0.965f, 0.32f);
		addComponent(new TextComponent("Speed multiplier: ", EditProps.LABEL), 0.71f, 0.17f, 0.88f, 0.245f);
		addComponent(speedMultiplier, 0.895f, 0.17f, 0.965f, 0.245f);
		addComponent(new TextComponent("knockback strength: ", EditProps.LABEL), 0.71f, 0.095f, 0.9f, 0.17f);
		addComponent(knockbackStrength, 0.9f, 0.095f, 0.95f, 0.17f);
		addComponent(new TextComponent("Arrow gravity", EditProps.LABEL), 0.8f, 0.02f, 0.95f, 0.095f);
		addComponent(gravity, 0.75f, 0.02f, 0.775f, 0.045f);
	}

	@Override
	protected boolean allowTexture(NamedImage texture) {
		return texture instanceof BowTextures;
	}

	@Override
	protected CustomBow previous() {
		return previous;
	}

	@Override
	protected String create(short damage, long maxUses, int entityHitDurabilityLoss, int blockBreakDurabilityLoss) {
		Option.Double damageMultiplier = this.damageMultiplier.getDouble();
		if (!damageMultiplier.hasValue()) return "The damage multiplier must be a positive number";
		Option.Double speedMultiplier = this.speedMultiplier.getDouble();
		if (!speedMultiplier.hasValue()) return "The speed multiplier must be a positive number";
		Option.Int knockbackStrength = this.knockbackStrength.getInt();
		if (!knockbackStrength.hasValue()) return "The knockback strength must be a positive integer";
		Option.Int shootDurabilityLoss = this.shootDurabilityLoss.getInt();
		if (!shootDurabilityLoss.hasValue()) return "The shoot durability loss must be a positive integer";
		return menu.getSet().addBow(new CustomBow(damage, name.getText(), getDisplayName(), lore, attributes, 
				enchantments, maxUses, damageMultiplier.getValue(), speedMultiplier.getValue(), 
				knockbackStrength.getValue(), gravity.isChecked(), allowEnchanting.isChecked(), 
				allowAnvil.isChecked(), repairItem.getIngredient(),
				(BowTextures) textureSelect.currentTexture, itemFlags, entityHitDurabilityLoss,
				blockBreakDurabilityLoss, shootDurabilityLoss.getValue()), true);
	}

	@Override
	protected String apply(short damage, long maxUses, int entityHitDurabilityLoss, int blockBreakDurabilityLoss) {
		Option.Double damageMultiplier = this.damageMultiplier.getDouble();
		if (!damageMultiplier.hasValue()) return "The damage multiplier must be a positive number";
		Option.Double speedMultiplier = this.speedMultiplier.getDouble();
		if (!speedMultiplier.hasValue()) return "The speed multiplier must be a positive number";
		Option.Int knockbackStrength = this.knockbackStrength.getInt();
		if (!knockbackStrength.hasValue()) return "The knockback strength must be a positive integer";
		Option.Int shootDurabilityLoss = this.shootDurabilityLoss.getInt();
		if (!shootDurabilityLoss.hasValue()) return "The shoot durability loss must be a positive integer";
		return menu.getSet().changeBow(previous, damage, name.getText(), getDisplayName(), lore, attributes, 
				enchantments, damageMultiplier.getValue(), speedMultiplier.getValue(), 
				knockbackStrength.getValue(), gravity.isChecked(), allowEnchanting.isChecked(), 
				allowAnvil.isChecked(), repairItem.getIngredient(), maxUses, 
				(BowTextures) textureSelect.currentTexture, itemFlags, entityHitDurabilityLoss,
				blockBreakDurabilityLoss, shootDurabilityLoss.getValue(), true);
	}
}