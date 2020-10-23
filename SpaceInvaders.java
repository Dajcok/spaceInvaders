package com.spaceinvaders;
import org.jsfml.graphics.*;
import org.jsfml.system.Vector2f;
import org.jsfml.system.Vector2i;
import org.jsfml.window.*;
import org.jsfml.window.event.*;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Vector;

class projectile {
    RectangleShape projectile = new RectangleShape();
    private Vector2f size;
    private Vector2f position;

    projectile(Vector2f size, Vector2f position) {
        this.position = position;
        this.size = size;

        this.projectile.setSize(this.size);
        this.projectile.setPosition(this.position);
        this.projectile.setOrigin(Vector2f.div(this.size, 2));
    }

    void updatePosition(Vector2f position) {
        this.position = position;
        this.projectile.setPosition(this.position);
    }

    Vector2f getPosition()
    {
        return this.position;
    }
}

class texturesLoader
{
    private Texture texture = new Texture();
    Sprite sprite = null;
    private boolean textureLoaded;
    private Vector2f textureScale;
    private Vector2f spritePos;

    texturesLoader(String filePath, Vector2f textureScale)
    {
        this.textureScale = textureScale;

        try
        {
            this.texture.loadFromFile(Paths.get(filePath));
            System.out.println("Textúra " + filePath + " úspešne načítaná");

            this.textureLoaded = true;

        }
        catch(IOException ex)
        {
            ex.printStackTrace();
            this.textureLoaded = false;
        }
    }

    Sprite createSprite()
    {
        if(this.textureLoaded)
        {
            Sprite sprite = new Sprite(this.texture);
            sprite.setScale(this.textureScale);
            return sprite;
        }
        else
        {
            System.out.println("Sprite z textúry nemohol byť vytvorený");

            return null;
        }
    }

    boolean updatePosition(Vector2f position)
    {
        this.spritePos = position;

        if(this.sprite == null)
        {
            this.sprite = createSprite();
            return false;
        }

        else
        {
            this.sprite.setOrigin(Vector2f.div(new Vector2f(texture.getSize()), 2));
            this.sprite.setPosition(this.spritePos);
            return true;
        }
    }

    Vector2f getSpritePos()
    {
        return this.spritePos;
    }
}

class targetWindow
{
    targetWindow(int W, int H, int frameLimit)
    {
        boolean projectileFired = false;
        projectile myProjectile = null;
        RenderWindow window = new RenderWindow();
        window.create(new VideoMode(W, H), "Space Invaders");
        window.setFramerateLimit(frameLimit);

        texturesLoader ship = new texturesLoader("ship.png", new Vector2f(0.2f, 0.2f));
        while(!ship.updatePosition(new Vector2f(100.f, 1000.f)));
        //texturesLoader enemy = new texturesLoader("enemy1.png", new Vector2f(0.2f, 0.2f));
        //while(!enemy.updatePosition(new Vector2f(100.f, 400.f)));
        Vector<texturesLoader> enemies1 = new Vector<>();
        //enemies1.add(texturesLoader());

        while(window.isOpen())
        {
            window.clear(Color.BLACK);
            window.draw(ship.sprite);
            window.draw(enemy.sprite);
            if(myProjectile != null)
                window.draw(myProjectile.projectile);
            window.display();

            if(projectileFired)
            {
                myProjectile.updatePosition(new Vector2f(myProjectile.getPosition().x, myProjectile.getPosition().y - 50));
                if(myProjectile.getPosition().y < 0)
                    projectileFired = false;
            }

            for(Event event : window.pollEvents())
            {
                switch(event.type) {
                    case CLOSED:
                        window.close();
                        break;

                    case KEY_PRESSED:
                        KeyEvent keyEvent = event.asKeyEvent();
                        if(keyEvent.key == Keyboard.Key.RIGHT && ship.getSpritePos().x < 1850)
                            ship.updatePosition(new Vector2f(ship.getSpritePos().x + 20.f, ship.getSpritePos().y));
                        else if(keyEvent.key == Keyboard.Key.LEFT && ship.getSpritePos().x > 100)
                            ship.updatePosition(new Vector2f(ship.getSpritePos().x - 20.f, ship.getSpritePos().y));
                        else if(keyEvent.key == Keyboard.Key.UP && !projectileFired)
                        {
                            myProjectile = new projectile(new Vector2f(5, 50), ship.getSpritePos());
                            projectileFired = true;
                        }
                        break;
                }
                if(event.type == Event.Type.CLOSED)
                {
                    window.close();
                }
            }
        }
    }
}

public class SpaceInvaders
{
    public static void main(String[] args)
    {
        targetWindow window = new targetWindow(1920, 1080, 60);
    }
}
