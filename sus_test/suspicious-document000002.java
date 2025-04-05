package com.braintreegateway;

public class CreditCardVerificationSearchRequest extends SearchRequest {
    public TextNode<CreditCardVerificationSearchRequest> id() {
        return new TextNode<CreditCardVerificationSearchRequest>("id", this);
    }

    public TextNode<CreditCardVerificationSearchRequest> creditCardCardholderName() {
        return new TextNode<CreditCardVerificationSearchRequest>("credit_card_cardholder_name", this);
    }

    public EqualityNode<CreditCardVerificationSearchRequest> creditCardExpirationDate() {
        return new EqualityNode<CreditCardVerificationSearchRequest>("credit_card_expiration_date", this);
    }

    public PartialMatchNode<CreditCardVerificationSearchRequest> creditCardNumber() {
        return new PartialMatchNode<CreditCardVerificationSearchRequest>("credit_card_number", this);
    }

    public MultipleValueNode<CreditCardVerificationSearchRequest, String> ids() {
        return new MultipleValueNode<CreditCardVerificationSearchRequest, String>("ids", this);
    }

    public MultipleValueNode<CreditCardVerificationSearchRequest, CreditCard.CardType> creditCardCardType() {
        return new MultipleValueNode<CreditCardVerificationSearchRequest, CreditCard.CardType>("credit_card_card_type", this);
    }

    public DateRangeNode<CreditCardVerificationSearchRequest> createdAt() {
        return new DateRangeNode<CreditCardVerificationSearchRequest>("created_at", this);
    }

    public MultipleValueNode<CreditCardVerificationSearchRequest, CreditCardVerification.Status> status() {
        return new MultipleValueNode<CreditCardVerificationSearchRequest, CreditCardVerification.Status>("status", this);
    }

    public TextNode<CreditCardVerificationSearchRequest> billingPostalCode() {
        return new TextNode<CreditCardVerificationSearchRequest>("billing_address_details_postal_code", this);
    }

    public TextNode<CreditCardVerificationSearchRequest> customerEmail() {
        return new TextNode<CreditCardVerificationSearchRequest>("customer_email", this);
    }

    public TextNode<CreditCardVerificationSearchRequest> customerId() {
        return new TextNode<CreditCardVerificationSearchRequest>("customer_id", this);
    }

    public TextNode<CreditCardVerificationSearchRequest> paymentMethodToken() {
        return new TextNode<CreditCardVerificationSearchRequest>("payment_method_token", this);
    }
}

void downloadProgress(Progress progress);
}
public CreeperMock(@NotNull ServerMock server, @NotNull UUID uuid)
	{
		super(server, uuid);
	}

	@Override
	public boolean isPowered()
	{
		return this.powered;
	}

	@Override
	public void setPowered(boolean value)
	{
		CreeperPowerEvent.PowerCause cause = powered ? CreeperPowerEvent.PowerCause.SET_ON : CreeperPowerEvent.PowerCause.SET_OFF;

		if (new CreeperPowerEvent(this, cause).callEvent())
		{
			this.powered = value;
		}
	}

	@Override
	public void setMaxFuseTicks(int ticks)
	{
		Preconditions.checkArgument(ticks >= 0, "Ticks need to be bigger than 0");
		this.maxFuseTicks = ticks;
	}

	@Override
	public int getMaxFuseTicks()
	{
		return this.maxFuseTicks;
	}

	@Override
	public void setFuseTicks(int ticks)
	{
		Preconditions.checkArgument(ticks >= 0, "Ticks need to be bigger than 0");
		Preconditions.checkArgument(ticks <= this.getMaxFuseTicks(), "Ticks need to be smaller than maxFuseTicks");
		this.fuseTicks = ticks;
	}

	@Override
	public int getFuseTicks()
	{
		return this.fuseTicks;
	}

	@Override
	public void setExplosionRadius(int radius)
	{
		Preconditions.checkArgument(radius >= 0, "Radius needs to be bigger than 0");
		this.explosionRadius = radius;
	}

	@Override
	public int getExplosionRadius()
	{
		return this.explosionRadius;
	}

	@Override
	public void explode()
	{
		float f = this.isPowered() ? 2.0F : 1.0F;
		ExplosionPrimeEvent event = new ExplosionPrimeEvent(this, this.getExplosionRadius() * f, false);
		this.getServer().getPluginManager().callEvent(event);
		this.remove();
	}

	@Override
	public void ignite()
	{
		setIgnited(true);
	}

	@Override
	public void setIgnited(boolean ignited)
	{
		if (isIgnited() == ignited)
		{
			return;
		}

		CreeperIgniteEvent event = new CreeperIgniteEvent(this, ignited);
		if (event.callEvent())
		{
			this.ignited = ignited;
		}
	}

	@Override
	public boolean isIgnited()
	{
		return this.ignited;
	}

	@Override
	public @NotNull EntityType getType()
	{
		return EntityType.CREEPER;
	}

}
public class BeaconAlchemyCircleRenderer extends AlchemyArrayRenderer
{
	public BeaconAlchemyCircleRenderer(ResourceLocation arrayResource)
	{
		super(arrayResource);
	}

	@Override
	public float getRotation(float craftTime)
	{
		float offset = 2;
		if (craftTime >= offset)
		{
			float modifier = (craftTime - offset) * 5f;
			return modifier * 1f;
		}
		return 0;
	}

	@Override
	public float getSecondaryRotation(float craftTime)
	{
		float offset = 50;
		float secondaryOffset = 150;
		if (craftTime >= offset)
		{
			if (craftTime < secondaryOffset)
			{
				float modifier = 90 * (craftTime - offset) / (secondaryOffset - offset);
				return modifier;
			} else
			{
				return 90;
			}
		}
		return 0;
	}

	public float getSizeModifier(float craftTime)
	{
		return 1.0f;
	}


	public void renderAt(TileAlchemyArray tileArray, double x, double y, double z, float craftTime, PoseStack matrixStack, MultiBufferSource renderer, int combinedLightIn, int combinedOverlayIn)
	{
		matrixStack.pushPose();

		matrixStack.translate(0.5, 0.5, 0.5);

		float rot = getRotation(craftTime);
		float secondaryRot = getSecondaryRotation(craftTime);

		float size = 1.0F * getSizeModifier(craftTime);
		Direction rotation = tileArray.getRotation();

		matrixStack.pushPose();
		matrixStack.translate(0, getVerticalOffset(craftTime), 0);
		matrixStack.mulPose(new Quaternion(Direction.UP.step(), -rotation.toYRot(), true));

		matrixStack.pushPose();

		matrixStack.mulPose(new Quaternion(Direction.UP.step(), rot, true));
		matrixStack.mulPose(new Quaternion(Direction.EAST.step(), -secondaryRot, true));

		VertexConsumer twoDBuffer = renderer.getBuffer(RenderType.entityTranslucent(arrayResource));
		Model2D arrayModel = new BloodMagicRenderer.Model2D();
		arrayModel.minX = -0.5;
		arrayModel.maxX = +0.5;
		arrayModel.minY = -0.5;
		arrayModel.maxY = +0.5;
		arrayModel.resource = arrayResource;

		matrixStack.scale(size, size, size);

		RenderResizableQuadrilateral.INSTANCE.renderSquare(arrayModel, matrixStack, twoDBuffer, 0xFFFFFFFF, 0x00F000F0, combinedOverlayIn);

		matrixStack.popPose();
		matrixStack.popPose();
		matrixStack.popPose();
	}
}
