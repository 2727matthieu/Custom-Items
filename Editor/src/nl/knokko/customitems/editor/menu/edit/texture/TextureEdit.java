package nl.knokko.customitems.editor.menu.edit.texture;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import nl.knokko.customitems.editor.menu.edit.EditMenu;
import nl.knokko.customitems.editor.menu.edit.EditProps;
import nl.knokko.customitems.editor.menu.main.CreateMenu;
import nl.knokko.customitems.editor.set.item.NamedImage;
import nl.knokko.gui.color.GuiColor;
import nl.knokko.gui.component.WrapperComponent;
import nl.knokko.gui.component.menu.FileChooserMenu;
import nl.knokko.gui.component.menu.GuiMenu;
import nl.knokko.gui.component.image.SimpleImageComponent;
import nl.knokko.gui.component.text.TextButton;
import nl.knokko.gui.component.text.TextComponent;
import nl.knokko.gui.component.text.TextEditField;

public class TextureEdit extends GuiMenu {
	
	protected final EditMenu menu;
	protected final NamedImage previous;
	protected final TextComponent errorComponent;
	
	protected TextEditField name;
	protected BufferedImage image;
	protected WrapperComponent<SimpleImageComponent> wrapper;

	public TextureEdit(EditMenu menu, NamedImage previous) {
		this.menu = menu;
		this.previous = previous;
		errorComponent = new TextComponent("", EditProps.ERROR);
		wrapper = new WrapperComponent<SimpleImageComponent>(null);
	}
	
	@Override
	public GuiColor getBackgroundColor() {
		return EditProps.BACKGROUND;
	}

	@Override
	protected void addComponents() {
		addComponent(errorComponent, 0.1f, 0.9f, 0.9f, 1f);
		addComponent(new TextButton("Cancel", EditProps.CANCEL_BASE, EditProps.CANCEL_HOVER, () -> {
			state.getWindow().setMainComponent(menu.getTextureOverview());
		}), 0.1f, 0.7f, 0.25f, 0.8f);
		if(previous != null) {
			name = new TextEditField(previous.getName(), EditProps.EDIT_BASE, EditProps.EDIT_ACTIVE);
			image = previous.getImage();
			wrapper.setComponent(new SimpleImageComponent(state.getWindow().getTextureLoader().loadTexture(image)));
		}
		else {
			name = new TextEditField("", EditProps.EDIT_BASE, EditProps.EDIT_ACTIVE);
		}
		addComponent(new TextComponent("Name: ", EditProps.LABEL), 0.4f, 0.6f, 0.55f, 0.7f);
		addComponent(name, 0.6f, 0.6f, 0.9f, 0.7f);
		addComponent(new TextComponent("Texture: ", EditProps.LABEL), 0.4f, 0.4f, 0.55f, 0.5f);
		addComponent(wrapper, 0.6f, 0.4f, 0.7f, 0.5f);
		addComponent(createImageSelect(), 0.75f, 0.4f, 0.9f, 0.5f);
		addComponent(new TextButton(previous != null ? "Save" : "Create", EditProps.SAVE_BASE, EditProps.SAVE_HOVER, () -> {
			if(image != null) {
				String error = CreateMenu.testFileName(name.getText() + ".png");
				if(error != null)
					errorComponent.setText(error);
				else {
					if(previous != null) {
						error = menu.getSet().changeTexture(previous, name.getText(), image);
						if(error != null)
							errorComponent.setText(error);
						else
							state.getWindow().setMainComponent(menu.getTextureOverview());
					}
					else {
						error = menu.getSet().addTexture(new NamedImage(name.getText(), image));
						if(error != null)
							errorComponent.setText(error);
						else
							state.getWindow().setMainComponent(menu.getTextureOverview());
					}
				}
			} else
				errorComponent.setText("You have to select an image before you can create this.");
		}), 0.4f, 0.3f, 0.6f, 0.4f);
	}
	
	private TextButton createImageSelect() {
		return new TextButton("Edit...", EditProps.CHOOSE_BASE, EditProps.CHOOSE_HOVER, () -> {
			state.getWindow().setMainComponent(new FileChooserMenu(TextureEdit.this, (File file) -> {
				try {
					BufferedImage loaded = ImageIO.read(file);
					if(loaded != null) {
						int width = loaded.getWidth();
						if(width == loaded.getHeight()) {
							if(width >= 16) {
								if(width <= 512) {
									if(width == 16 || width == 32 || width == 64 || width == 128 || width == 256 || width == 512) {
										image = loaded;
										wrapper.setComponent(new SimpleImageComponent(state.getWindow().getTextureLoader().loadTexture(image)));
									} else
										errorComponent.setText("The width and height (" + width + ") should be a power of 2");
								} else
									errorComponent.setText("The image should be at most 512 x 512 pixels.");
							} else
								errorComponent.setText("The image should be at least 16 x 16 pixels.");
						} else
							errorComponent.setText("The width (" + image.getWidth() + ") of this image should be equal to the height (" + image.getHeight() + ")");
					} else
						errorComponent.setText("This image can't be read.");
				} catch(IOException ioex) {
					errorComponent.setText("IO error: " + ioex.getMessage());
				}
			}, (File file) -> {
				return file.getName().endsWith(".png");
			}));
		});
	}
}