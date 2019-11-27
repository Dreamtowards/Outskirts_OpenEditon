package outskirts.client.render.chunk;

import outskirts.client.material.ModelData;
import outskirts.client.render.renderer.GuiRenderer;
import outskirts.client.render.renderer.ModelRenderer;
import outskirts.util.CollectionUtils;
import outskirts.util.Maths;
import outskirts.util.vector.Vector2f;
import outskirts.util.vector.Vector3f;
import outskirts.util.vector.Vector3i;
import outskirts.world.World;
import outskirts.world.chunk.Octree;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ChunkModelGenerator {

    public static ChunkModelGenerator INSTANCE = new ChunkModelGenerator();

    public ModelData generateModel(World world, Octree root, Vector3i start) {
        List<Float> positions = new ArrayList<>();
        List<Float> textureCoords = new ArrayList<>();
        List<Float> normals = new ArrayList<>();

        putOctree(world, root, start.x, start.y, start.z, positions, textureCoords, normals);

        return new ModelData(
                null,
                CollectionUtils.toArray(positions),
                CollectionUtils.toArray(textureCoords),
                CollectionUtils.toArray(normals)
        );
    }

    private void putOctree(World world, Octree octree, float x, float y, float z, List<Float> positions, List<Float> textureCoords, List<Float> normals) {

        if (octree.hasChildren()) {
            float size = octree.size();
            for (int i = 0;i < Octree.LENGTH;i++) {
                putOctree(world, octree.child(i),
                        x + Octree.CHILDREN_POS[i].x*16f*size,
                        y + Octree.CHILDREN_POS[i].y*16f*size,
                        z + Octree.CHILDREN_POS[i].z*16f*size,
                        positions, textureCoords, normals
                );
            }
        }
        else if (octree.hasBody())
        {
            octree.body().getBlock().getVertexData(world, new Vector3f(x, y, z), octree, positions, textureCoords, normals);
        }
    }



    private static float[] EMPTY_ARR3 = {0, 0, 0};

    public static void putCube(World world, List<Float> positions, List<Float> textureCoords, List<Float> normals, float x, float y, float z, int depth, float size, Vector2f texOffset, Vector2f texScale) {

        Vector2f[] facesTexOffsets = {
                new Vector2f(mod1(z), mod1(y)), //-X
                new Vector2f(mod1(1-z-size), mod1(y)), //+X
                new Vector2f(mod1(x), mod1(z)), //-Y
                new Vector2f(mod1(x), mod1(1-z-size)), //+Y
                new Vector2f(mod1(1-x-size), mod1(y)), //-Z
                new Vector2f(mod1(x), mod1(y))  //+Z
        };
        for (Vector2f tOf : facesTexOffsets) {
            tOf.x *= texScale.x;
            tOf.y *= texScale.y;
        }
        for (int i = 0;i < 6;i++) {
            Vector3f dir = ModelRenderer.DATAM_CUBE_FACES_DIRECTIONS[i];
            if (world.getBlockState(x + dir.x*size, y + dir.y*size, z + dir.z*size, depth, Octree.GettingType.DIRECT) == null) { //-X

                addArrayToList(ModelRenderer.DATA_CUBE_POSITIONS, i*6*3, (i+1)*6*3, positions, 3, new float[]{x, y, z}, new float[]{size,size,size});

                addArrayToList(ModelRenderer.DATA_CUBE_TEXTURECOORDS, 0, 12, textureCoords, 2, new float[]{texOffset.x+facesTexOffsets[i].x, texOffset.y+facesTexOffsets[i].y}, new float[]{texScale.x*size,texScale.y*size});

                addArrayToList(ModelRenderer.DATA_CUBE_NORMALS, i*6*3, (i+1)*6*3, normals, 3, EMPTY_ARR3, new float[]{1,1,1});
            }
        }

    }

    private static void addArrayToList(float[] array, int start, int end, List<Float> list,
                                       int vertexSize, float[] vertexOffsets, float[] vertexScales) {
        for (int i = start;i < end;) {
            for (int j = 0;j < vertexSize;j++) {
//                list.add((vertexOffsets.length == 2 ? array[i++] - vertexOffsets[j] : array[i++]) * rawVertexScale + vertexOffsets[j]);
                list.add(array[i++] * vertexScales[j] + vertexOffsets[j]);
            }
        }
    }

    private static float mod1(float v) {
        return Maths.mod(v, 1);
    }
}
