package com.spaceinvaders;
import org.jsfml.graphics.*;
import org.jsfml.system.Vector2f;
import org.jsfml.system.Vector2i;
import org.jsfml.window.*;
import org.jsfml.window.event.*;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Iterator;
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
    Vector2f enemies1Pos = new Vector2f(0, 0);
    int mark = 1;
    boolean changeDir = false;

    targetWindow(int W, int H, int frameLimit)
    {
        int enemies1PosX = 0;
        int enemies2PosX = 0;
        int enemies3PosX = 0;

        boolean projectileFired = false;

        projectile myProjectile = null;
        RenderWindow window = new RenderWindow();
        window.create(new VideoMode(W, H), "Space Invaders");
        window.setFramerateLimit(frameLimit);

        texturesLoader ship = new texturesLoader("ship.png", new Vector2f(0.2f, 0.2f));
        while(!ship.updatePosition(new Vector2f(100.f, 1000.f)));

        Vector<texturesLoader> enemies1 = new Vector<>();

        for(int i = 0; i < 18; i++)
        {
            enemies1.addElement(new texturesLoader("enemy1.png", new Vector2f(0.2f, 0.2f)));
            while(!enemies1.elementAt(i).updatePosition(new Vector2f(100.f + enemies1PosX, 400.f)));
            enemies1PosX += 100;
        }

        Vector<texturesLoader> enemies2 = new Vector<>();

        for(int i = 0; i < 18; i++)
        {
            enemies2.addElement(new texturesLoader("enemy2.png", new Vector2f(0.12f, 0.12f)));
            while(!enemies2.elementAt(i).updatePosition(new Vector2f(100.f + enemies2PosX, 300.f)));
            enemies2PosX += 100;
        }

        Vector<texturesLoader> enemies3 = new Vector<>();

        for(int i = 0; i < 18; i++)
        {
            enemies3.addElement(new texturesLoader("enemy3.png", new Vector2f(0.1f, 0.1f)));
            while(!enemies3.elementAt(i).updatePosition(new Vector2f(100.f + enemies3PosX, 200.f)));
            enemies3PosX += 100;
        }

        while(window.isOpen())
        {
            window.clear(Color.BLACK);
            window.draw(ship.sprite);
            for(int i = 0; i < enemies1.size(); i++)
                window.draw(enemies1.elementAt(i).sprite);
            for(int i = 0; i < enemies2.size(); i++)
                window.draw(enemies2.elementAt(i).sprite);
            for(int i = 0; i < enemies3.size(); i++)
                window.draw(enemies3.elementAt(i).sprite);
            if(myProjectile != null)
                window.draw(myProjectile.projectile);
            window.display();

            for(int i = 0; i < enemies1.size(); i++)
            {
                if(enemies1.elementAt(i).getSpritePos().x > 1870)
                {
                    this.changeDir = true;
                    break;
                }

                else
                    enemyMovement(i, enemies1);

            }

            if(projectileFired && myProjectile != null)
            {
                myProjectile.updatePosition(new Vector2f(myProjectile.getPosition().x, myProjectile.getPosition().y - 50));
                if(myProjectile.getPosition().y < 0)
                    projectileFired = false;
            }


            if(myProjectile != null)
            {
                A:
                for(int i = 0; i < enemies1.size() + 1; i++)
                {
                        if (enemies1.size() != i && hitCheck(myProjectile.getPosition(), enemies1.elementAt(i).getSpritePos()))
                        {
                            myProjectile = null;
                            projectileFired = false;
                            enemies1.remove(i);
                            break A;
                        }
                        else
                            for (int j = 0; j < enemies2.size() + 1; j++) {
                                if (enemies2.size() != j && hitCheck(myProjectile.getPosition(), enemies2.elementAt(j).getSpritePos()))
                                {
                                    myProjectile = null;
                                    projectileFired = false;
                                    enemies2.remove(j);
                                    break A;
                                }
                                else
                                    for (int k = 0; k < enemies3.size(); k++)
                                        if (hitCheck(myProjectile.getPosition(), enemies3.elementAt(k).getSpritePos()))
                                        {
                                            myProjectile = null;
                                            projectileFired = false;
                                            enemies3.remove(k);
                                            break A;
                                        }
                        }
                }
            }


            for(Event event : window.pollEvents())
            {
                switch(event.type)
                {
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

    boolean hitCheck(Vector2f bulletPos, Vector2f targetPos)
    {
        if((bulletPos.x > targetPos.x - 25 && bulletPos.y == targetPos.y) && (bulletPos.x < targetPos.x + 25 && bulletPos.y == targetPos.y))
            return true;
        else
            return false;
    }

    void enemyMovement(int i, Vector<texturesLoader> enemies1)
    {
        if(this.changeDir)
        {
            this.mark = this.mark * -1;
        }
        enemies1.elementAt(i).updatePosition(new Vector2f (enemies1.elementAt(i).getSpritePos().x + (this.mark * this.enemies1Pos.x), enemies1.elementAt(i).getSpritePos().y + (this.mark * this.enemies1Pos.y)));
        this.enemies1Pos = new Vector2f(this.enemies1Pos.x + 0.0001f, this.enemies1Pos.y + 0);
    }
}

public class SpaceInvaders
{
    public static void main(String[] args)
    {
        targetWindow window = new targetWindow(1920, 1080, 60);
    }
}
